package org.upm.inesdata.federated.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.eclipse.edc.connector.controlplane.contract.spi.types.offer.ContractOffer;

/**
 * This interface represents the API for accessing the Federated Catalog. It provides operations to obtain catalogs with
 * dataset pagination.
 */
@OpenAPIDefinition(
    info = @Info(description = "This represents the Federated Catalog API.", title = "Federated Catalog API",
        version = "1"))
@Tag(name = "Federated Catalog")
public interface FederatedCatalogCacheApi {
  /**
   * Obtains all catalogs with dataset pagination based on the provided query specification.
   *
   * @param querySpec the specification of the query which includes filters, sorting, and pagination details.
   * @return a JsonArray containing the list of catalogs that match the query criteria, potentially empty.
   */
  @Operation(description = "Obtains all Catalog with dataset pagination", responses = {
      @ApiResponse(responseCode = "200", description = "A list of Catalog is returned, potentially empty",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContractOffer.class)))),
      @ApiResponse(responseCode = "500", description = "A Query could not be completed due to an internal error") }

  )
  JsonArray getFederatedCatalog(JsonObject querySpec);
}
