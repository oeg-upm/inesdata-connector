package org.upm.inesdata.countelements.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.json.JsonObject;
import org.eclipse.edc.api.model.ApiCoreSchema;

@OpenAPIDefinition(
    info = @Info(description = "CountElement the elements of an entity.",
        title = "CountElement Elements API", version = "1"))
@Tag(name = "CountElements")
public interface CountElementsApi {

    /**
     * Gets the total elements of an entity type.
     *
     * @param entityType entity type
     * @return the total number of elements
     */
    @Operation(description = "CountElement the elements of an entity",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The total number of elements",
                            content = @Content(schema = @Schema(implementation = Long.class))),
                    @ApiResponse(responseCode = "400", description = "Request was malformed, e.g. entityType was null",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiCoreSchema.ApiErrorDetailSchema.class)))),
                    @ApiResponse(responseCode = "404", description = "The entity type given does not exist",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiCoreSchema.ApiErrorDetailSchema.class))))
            }
    )
    long countElements(String entityType, JsonObject querySpecJson);
}
