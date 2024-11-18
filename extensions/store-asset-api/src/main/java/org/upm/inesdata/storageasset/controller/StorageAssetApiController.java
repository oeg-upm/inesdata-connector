package org.upm.inesdata.storageasset.controller;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.api.model.IdResponse;
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.connector.controlplane.services.spi.asset.AssetService;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.constants.CoreConstants;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.eclipse.edc.web.spi.exception.ValidationFailureException;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.upm.inesdata.storageasset.service.S3Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset.EDC_ASSET_TYPE;
import static org.eclipse.edc.web.spi.exception.ServiceResultHandler.exceptionMapper;

@MultipartConfig
@Path("/s3assets")
public class StorageAssetApiController implements StorageAssetApi {

  private final TypeTransformerRegistry transformerRegistry;
  private final AssetService service;
  private final JsonObjectValidatorRegistry validator;
  private final S3Service s3Service;
  private final JsonLd jsonLd;
  private final String bucketName;
  private final String region;

  public StorageAssetApiController(AssetService service, TypeTransformerRegistry transformerRegistry,
                                   JsonObjectValidatorRegistry validator, S3Service s3Service, JsonLd jsonLd,
                                   String bucketName, String region) {
    this.transformerRegistry = transformerRegistry;
    this.service = service;
    this.validator = validator;
    this.s3Service = s3Service;
    this.jsonLd = jsonLd;
    this.bucketName = bucketName;
    this.region = region;
  }

  /**
   * Handles each chunk upload
   */
  @POST
  @Path("/upload-chunk")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject uploadChunk(@HeaderParam("Content-Disposition") String contentDisposition,
                                @HeaderParam("Chunk-Index") int chunkIndex,
                                @HeaderParam("Total-Chunks") int totalChunks,
                                @FormDataParam("json") JsonObject assetJson,
                                @FormDataParam("file") InputStream fileInputStream) {

    JsonObject expand = jsonLd.expand(assetJson).orElseThrow((f) -> new EdcException("Failed to expand request"));

    validator.validate(EDC_ASSET_TYPE, expand).orElseThrow(ValidationFailureException::new);
    Asset asset = transformerRegistry.transform(expand, Asset.class).orElseThrow(InvalidRequestException::new);

    String fileName = contentDisposition.split("filename=")[1].replace("\"", "");
    String folder = String.valueOf(asset.getDataAddress().getProperties().get(CoreConstants.EDC_NAMESPACE+"folder"));

    // Construct the S3 key for the file, keeping the folder structure
    String fullKey;
    if (folder == null || folder.trim().isEmpty() || "null".equals(folder)) {
      fullKey = fileName;  // No folder, use the file name
    } else {
      fullKey = folder.endsWith("/") ? folder + fileName : folder + "/" + fileName;
    }

    // Handle file upload chunking
    try {
      s3Service.uploadChunk(fullKey, fileInputStream, chunkIndex, totalChunks);

      // Return successful upload status for each chunk
      return Json.createObjectBuilder()
              .add("status", "Chunk " + chunkIndex + " uploaded successfully")
              .build();
    } catch (IOException e) {
      // If an error occurs, delete the file from S3
      s3Service.deleteFile(fullKey);
      throw new EdcException("Failed to read or upload chunk", e);
    } catch (Exception e) {
      // If an error occurs, delete the file from S3
      s3Service.deleteFile(fullKey);
      throw new EdcException("Failed to process chunked data", e);
    }
  }


  /**
   * Finalize upload and create asset with JSON data
   */
  @POST
  @Path("/finalize-upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject finalizeUpload(@FormDataParam("fileName") String fileName,
                                   @FormDataParam("json") JsonObject assetJson) {

    JsonObject expand = jsonLd.expand(assetJson).orElseThrow((f) -> new EdcException("Failed to expand request"));
    
    validator.validate(EDC_ASSET_TYPE, expand).orElseThrow(ValidationFailureException::new);
    Asset asset = transformerRegistry.transform(expand, Asset.class).orElseThrow(InvalidRequestException::new);

    // Set storage properties for the asset
    setStorageProperties(asset, fileName);

    // Create the asset in the service
    IdResponse idResponse = service.create(asset)
            .map(a -> IdResponse.Builder.newInstance().id(a.getId()).createdAt(a.getCreatedAt()).build())
            .orElseThrow(exceptionMapper(Asset.class, asset.getId()));

    // Return the response for the created asset
    return transformerRegistry.transform(idResponse, JsonObject.class)
            .orElseThrow(f -> new EdcException(f.getFailureDetail()));
  }

  /**
   * Set necessary storage properties for the asset in S3.
   */
  private void setStorageProperties(Asset asset, String fileName) {
    asset.getPrivateProperties().put("storageAssetFile", fileName);
    asset.getDataAddress().setKeyName(fileName);
    asset.getDataAddress().setType("AmazonS3");
    asset.getDataAddress().getProperties().put(CoreConstants.EDC_NAMESPACE + "bucketName", bucketName);
    asset.getDataAddress().getProperties().put(CoreConstants.EDC_NAMESPACE + "region", region);
  }
}
