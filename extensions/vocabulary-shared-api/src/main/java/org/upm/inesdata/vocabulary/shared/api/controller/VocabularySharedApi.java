package org.upm.inesdata.vocabulary.shared.api.controller;

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
import org.eclipse.edc.connector.controlplane.contract.spi.types.offer.ContractOffer;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;
import org.upm.inesdata.vocabulary.controller.VocabularyApi;

import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.EDC_VOCABULARY_TYPE;

/**
 * Controller for getting {@link Vocabulary}.
 */
@OpenAPIDefinition(
        info = @Info(description = "Gets the connectors vocabularies.",
                title = "Vocabulary Shared API", version = "1"))
@Tag(name = "Shared Vocabulary")
public interface VocabularySharedApi {

    /**
     * Get all the vocabularies of a connector.
     *
     * @param connectorVocabularyJson the
     * @return list of vocabularies of a connector
     */
    @Operation(description = "Obtains all vocabularies from a connector",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ConnectorVocabularyInputSchema.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of vocabularies of a connector",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Vocabulary.class))))
            }
      )
    JsonArray getVocabulariesFromConnector(JsonObject connectorVocabularyJson);

    /**
     * Get all the vocabularies stored in the vocabularies index. No filters are required due
     * to the limited number of vocabularies that each data space will manage.
     *
     * @return list of vocabularies
     */
    @Operation(description = "Obtains all vocabularies",
            responses = {
                    @ApiResponse(responseCode = "200", description = "A list of vocabularies",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContractOffer.class))))
            }
    )
    JsonArray getVocabularies();

    /**
     * Vocabulary output
     */
    @ArraySchema()
    @Schema(name = "ConnectorVocabularyInput", example = ConnectorVocabularyInputSchema.CONNECTOR_VOCABULARY_INPUT_EXAMPLE)
    record ConnectorVocabularyInputSchema(
            String connectorId
    ) {
        public static final String CONNECTOR_VOCABULARY_INPUT_EXAMPLE = """
                {
                    "connectorId": "connector-c1"
                }
                """;
    }

}
