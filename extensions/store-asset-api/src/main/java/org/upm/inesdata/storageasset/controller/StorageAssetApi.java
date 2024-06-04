package org.upm.inesdata.storageasset.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.api.model.ApiCoreSchema;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;

@OpenAPIDefinition(
    info = @Info(description = "Manages the connector s3 assets.",
        title = "S3 Asset API", version = "1"))
@Tag(name = "S3Asset")
public interface StorageAssetApi {

    /**
     * Creates a new storage asset
     *
     * @param fileInputStream the input stream of the file to be uploaded
     * @param fileDetail the details of the file to be uploaded
     * @param assetJson the input stream of the asset metadata in JSON format
     * @return JsonObject with the created asset
     */
    @Operation(description = "Creates a new S3 asset",
        requestBody = @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA, schema = @Schema(
            type = "object", requiredProperties = {"file", "json"}
        ))),
        responses = {
            @ApiResponse(responseCode = "200", description = "S3 asset was created successfully",
                content = @Content(schema = @Schema(implementation = ApiCoreSchema.IdResponseSchema.class))),
            @ApiResponse(responseCode = "400", description = "Request body was malformed",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiCoreSchema.ApiErrorDetailSchema.class)))),
            @ApiResponse(responseCode = "409", description = "Could not create asset, because an asset with that ID already exists",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiCoreSchema.ApiErrorDetailSchema.class))))
        }
    )
    JsonObject createStorageAsset(@FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @FormDataParam("json") JsonObject assetJson);
}
