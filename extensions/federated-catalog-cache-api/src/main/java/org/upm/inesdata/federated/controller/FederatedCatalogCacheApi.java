package org.upm.inesdata.federated.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.api.model.ApiCoreSchema;
import org.eclipse.edc.catalog.spi.model.FederatedCatalogCacheQuery;
import org.eclipse.edc.connector.controlplane.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.spi.query.QuerySpec;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;

@OpenAPIDefinition(
    info = @Info(description = "This represents the Federated Catalog API.",
        title = "Federated Catalog API", version = "1"))
@Tag(name = "Federated Catalog")
public interface FederatedCatalogCacheApi {
    @Operation(description = "Obtains all Catalog with dataset pagination",
        responses = {
            @ApiResponse(responseCode = "200", description = "A list of Catalog is returned, potentially empty",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContractOffer.class)))),
            @ApiResponse(responseCode = "500", description = "A Query could not be completed due to an internal error")
        }

    )
    JsonArray getFederatedCatalog(JsonObject querySpec);
}
