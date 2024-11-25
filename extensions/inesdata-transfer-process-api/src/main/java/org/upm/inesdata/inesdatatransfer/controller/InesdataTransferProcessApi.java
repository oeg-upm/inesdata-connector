package org.upm.inesdata.inesdatatransfer.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.links.LinkParameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.eclipse.edc.api.management.schema.ManagementApiSchema;
import org.eclipse.edc.api.model.ApiCoreSchema;
import org.eclipse.edc.connector.controlplane.api.management.transferprocess.v3.TransferProcessApiV3;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
import static org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferRequest.TRANSFER_REQUEST_TYPE;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.CONTEXT;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;

@OpenAPIDefinition(
    info = @Info(
        version = "v3"
    )
)
@Tag(
    name = "Transfer Process V3"
)
public interface InesdataTransferProcessApi {
  String ASYNC_WARNING = "Due to the asynchronous nature of transfers, a successful response only indicates that the request was successfully received. This may take a long time, so clients must poll the /{id}/state endpoint to track the state.";

  @Operation(
      description = "Initiates a data transfer with the given parameters. Due to the asynchronous nature of transfers, a successful response only indicates that the request was successfully received. This may take a long time, so clients must poll the /{id}/state endpoint to track the state.",
      requestBody = @RequestBody(
          content = {@Content(
              schema = @Schema(
                  implementation = TransferRequestSchema.class
              )
          )}
      ),
      responses = {@ApiResponse(
          responseCode = "200",
          description = "The transfer was successfully initiated. Returns the transfer process ID and created timestamp",
          content = {@Content(
              schema = @Schema(
                  implementation = ApiCoreSchema.IdResponseSchema.class
              )
          )},
          links = {@Link(
              name = "poll-state",
              operationId = "getTransferProcessStateV3",
              parameters = {@LinkParameter(
                  name = "id",
                  expression = "$response.body#/id"
              )}
          )}
      ), @ApiResponse(
          responseCode = "400",
          description = "Request body was malformed",
          content = {@Content(
              array = @ArraySchema(
                  schema = @Schema(
                      implementation = ApiCoreSchema.ApiErrorDetailSchema.class
                  )
              )
          )}
      )}
  )
  JsonObject initiateTransferProcess(JsonObject var1);


  @Schema(name = "TransferRequest", example = TransferProcessApiV3.TransferRequestSchema.TRANSFER_REQUEST_EXAMPLE)
  record TransferRequestSchema(
      @Schema(name = CONTEXT, requiredMode = REQUIRED)
      Object context,
      @Schema(name = TYPE, example = TRANSFER_REQUEST_TYPE)
      String type,
      @Schema(requiredMode = REQUIRED)
      String protocol,
      @Schema(requiredMode = REQUIRED)
      String counterPartyAddress,
      @Schema(requiredMode = REQUIRED)
      String contractId,
      @Schema(deprecated = true)
      String assetId,
      @Schema(requiredMode = REQUIRED)
      String transferType,
      ApiCoreSchema.DataAddressSchema dataDestination,
      @Schema(additionalProperties = Schema.AdditionalPropertiesValue.TRUE)
      ManagementApiSchema.FreeFormPropertiesSchema privateProperties,
      List<ManagementApiSchema.CallbackAddressSchema> callbackAddresses) {

    public static final String TRANSFER_REQUEST_EXAMPLE = """
                {
                    "@context": { "@vocab": "https://w3id.org/edc/v0.0.1/ns/" },
                    "@type": "https://w3id.org/edc/v0.0.1/ns/TransferRequest",
                    "protocol": "dataspace-protocol-http",
                    "counterPartyAddress": "http://provider-address",
                    "contractId": "contract-id",
                    "transferType": "transferType",
                    "dataDestination": {
                        "type": "data-destination-type"
                    },
                    "privateProperties": {
                        "private-key": "private-value"
                    },
                    "callbackAddresses": [{
                        "transactional": false,
                        "uri": "http://callback/url",
                        "events": ["contract.negotiation", "transfer.process"],
                        "authKey": "auth-key",
                        "authCodeId": "auth-code-id"
                    }]
                }
                """;
  }

}
