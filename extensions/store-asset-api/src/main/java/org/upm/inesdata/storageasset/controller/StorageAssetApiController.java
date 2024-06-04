package org.upm.inesdata.storageasset.controller;

import jakarta.json.JsonObject;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.api.model.IdResponse;
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.connector.controlplane.services.spi.asset.AssetService;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.constants.CoreConstants;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.util.string.StringUtils;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.eclipse.edc.web.spi.exception.ValidationFailureException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.upm.inesdata.storageasset.service.S3Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset.EDC_ASSET_TYPE;
import static org.eclipse.edc.web.spi.exception.ServiceResultHandler.exceptionMapper;

@MultipartConfig
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
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
      JsonObjectValidatorRegistry validator, S3Service s3Service, JsonLd jsonLd, String bucketName, String region) {
    this.transformerRegistry = transformerRegistry;
    this.service = service;
    this.validator = validator;
    this.s3Service = s3Service;
    this.jsonLd = jsonLd;
    this.bucketName = bucketName;
    this.region = region;
  }

  @POST
  @Override
  public JsonObject createStorageAsset(@FormDataParam("file") InputStream fileInputStream,
      @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("json") JsonObject assetJson) {

    String fileName = fileDetail.getFileName();

    InputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

    JsonObject expand = jsonLd.expand(assetJson).orElseThrow((f) -> new EdcException("Failed to expand request"));
    // Validación
    validator.validate(EDC_ASSET_TYPE, expand).orElseThrow(ValidationFailureException::new);

    // Transformación
    var asset = transformerRegistry.transform(expand, Asset.class).orElseThrow(InvalidRequestException::new);

    // Guardar fichero en MinIO
    // Calcular el tamaño del fichero manualmente
    long contentLength = 0;
    try {
      contentLength = getFileSize(bufferedInputStream);
    } catch (IOException e) {
      throw new EdcException("Failed to process file size", e);
    }
    String folder = String.valueOf(asset.getDataAddress().getProperties().get(CoreConstants.EDC_NAMESPACE+"folder"));
    String fullKey = StringUtils.isNullOrBlank(folder) || "null".equals(folder)?fileName:(folder.endsWith("/") ? folder + fileName : folder + "/" + fileName);
    s3Service.uploadFile(fullKey,bufferedInputStream, contentLength);
    try {
      setStorageProperties(asset, fullKey);

      // Creación de asset
      var idResponse = service.create(asset)
          .map(a -> IdResponse.Builder.newInstance().id(a.getId()).createdAt(a.getCreatedAt()).build())
          .orElseThrow(exceptionMapper(Asset.class, asset.getId()));

      return transformerRegistry.transform(idResponse, JsonObject.class)
          .orElseThrow(f -> new EdcException(f.getFailureDetail()));
    } catch (Exception e) {
      // Eliminar el archivo en caso de fallo
      s3Service.deleteFile(fullKey);
      throw new EdcException("Failed to process multipart data", e);
    }
  }

  private long getFileSize(InputStream inputStream) throws IOException {
    byte[] buffer = new byte[8192];
    int bytesRead;
    long size = 0;

    inputStream.mark(Integer.MAX_VALUE);

    while ((bytesRead = inputStream.read(buffer)) != -1) {
      size += bytesRead;
    }

    inputStream.reset();

    return size;
  }

  private void setStorageProperties(Asset asset, String fileName) {
    asset.getPrivateProperties().put("storageAssetFile", fileName);
    asset.getDataAddress().setKeyName(fileName);
    asset.getDataAddress().setType("InesDataStore");
    asset.getDataAddress().getProperties().put(CoreConstants.EDC_NAMESPACE+ "bucketName", bucketName);
    asset.getDataAddress().getProperties().put(CoreConstants.EDC_NAMESPACE+"region", region);
  }
}
