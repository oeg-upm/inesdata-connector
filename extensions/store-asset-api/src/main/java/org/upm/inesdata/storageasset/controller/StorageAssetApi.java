package org.upm.inesdata.storageasset.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.api.model.ApiCoreSchema;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;

@OpenAPIDefinition(
        info = @Info(description = "Manages the connector S3 assets.",
                title = "S3 Asset API", version = "1"))
@Tag(name = "S3Asset")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public interface StorageAssetApi {

    /**
     * Uploads a chunk of a file for creating a new S3 asset.
     *
     * @param contentDisposition The Content-Disposition header, which contains the file name.
     * @param chunkIndex       The index of the current chunk in the upload sequence.
     * @param totalChunks      The total number of chunks for this file.
     * @param assetJson The asset info
     * @param fileInputStream  The input stream of the file chunk to be uploaded.
     * @return JsonObject with status of the chunk upload or the next action.
     */
    @POST
    @Path("/upload-chunk")
    @Operation(
            description = "Uploads a chunk of a file to create a new S3 asset.",
            requestBody = @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA, schema = @Schema(
                    type = "object", requiredProperties = {"file", "json"}
            ))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Chunk uploaded successfully",
                            content = @Content(schema = @Schema(implementation = ApiCoreSchema.IdResponseSchema.class))),
                    @ApiResponse(responseCode = "400", description = "Request body was malformed",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiCoreSchema.ApiErrorDetailSchema.class)))),
                    @ApiResponse(responseCode = "409", description = "Could not upload chunk, because of conflicts",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiCoreSchema.ApiErrorDetailSchema.class))))
            }
    )
    JsonObject uploadChunk(
            @Parameter(description = "Content-Disposition header, which contains the file name") @HeaderParam("Content-Disposition") String contentDisposition,
            @Parameter(description = "Index of the current chunk in the upload sequence") @HeaderParam("Chunk-Index") int chunkIndex,
            @Parameter(description = "Total number of chunks for this file") @HeaderParam("Total-Chunks") int totalChunks,
            @FormDataParam("json") JsonObject assetJson,
            @FormDataParam("file") InputStream fileInputStream);

    /**
     * Finalizes the upload and creates the asset using the provided metadata (JSON).
     *
     * @param assetJson The metadata for the asset in JSON format.
     * @param fileName The name of the uploaded file.
     * @return JsonObject with the created asset or the status.
     */
    @POST
    @Path("/finalize-upload")
    @Operation(
            description = "Finalizes the chunked upload and creates the S3 asset using the provided metadata.",
            requestBody = @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA, schema = @Schema(
                    type = "object", requiredProperties = {"json", "fileName"}
            ))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Asset created successfully",
                            content = @Content(schema = @Schema(implementation = ApiCoreSchema.IdResponseSchema.class))),
                    @ApiResponse(responseCode = "400", description = "Request body was malformed",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiCoreSchema.ApiErrorDetailSchema.class)))),
                    @ApiResponse(responseCode = "409", description = "Asset could not be created due to conflict",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiCoreSchema.ApiErrorDetailSchema.class))))
            }
    )
    JsonObject finalizeUpload(
            @FormDataParam("fileName") String fileName,
            @FormDataParam("json") JsonObject assetJson);
}
