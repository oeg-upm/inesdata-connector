package org.upm.inesdata.vocabulary.controller;

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
import org.eclipse.edc.api.model.ApiCoreSchema;
import org.eclipse.edc.connector.controlplane.contract.spi.types.offer.ContractOffer;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;

import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.EDC_VOCABULARY_TYPE;

/**
 * Controller for managing {@link Vocabulary} objects.
 */
@OpenAPIDefinition(
        info = @Info(description = "Manages the connector vocabularies.",
                title = "Vocabulary API", version = "1"))
@Tag(name = "Vocabulary")
public interface VocabularyApi {

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
     * Retrieves the {@link Vocabulary} with the given ID 
     *
     * @param id id of the vocabulary
     * @return JsonObject with the vocabulary information
     */
    @Operation(description = "Gets a vocabulary with the given ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The vocabulary",
                            content = @Content(schema = @Schema(implementation = VocabularyOutputSchema.class))),
                    @ApiResponse(responseCode = "400", description = "Request was malformed, e.g. id was null",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiCoreSchema.ApiErrorDetailSchema.class)))),
                    @ApiResponse(responseCode = "404", description = "A vocabulary with the given ID does not exist",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiCoreSchema.ApiErrorDetailSchema.class))))
            }
    )
    JsonObject getVocabulary(String id);

    /**
     * Creates a new vocabulary
     *
     * @param vocabulary the vocabulary
     * @return JsonObject with the created vocabulary
     */
    @Operation(description = "Creates a new vocabulary",
    requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = VocabularyOutputSchema.class))),
    responses = {
            @ApiResponse(responseCode = "200", description = "Vocabulary was created successfully",
                    content = @Content(schema = @Schema(implementation = ApiCoreSchema.IdResponseSchema.class))),
            @ApiResponse(responseCode = "400", description = "Request body was malformed",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiCoreSchema.ApiErrorDetailSchema.class)))),
            @ApiResponse(responseCode = "409", description = "Could not create vocabulary, because a vocabulary with that ID already exists",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiCoreSchema.ApiErrorDetailSchema.class)))) }
    )
    JsonObject createVocabulary(JsonObject vocabylary);

    /**
     * Updates a vocabulary
     *
     * @param vocabulary the vocabulary to be updated
     * @return JsonObject with the updated vocabulary
     */
    @Operation(description = "Updates a vocabulary with the given ID if it exists. If the vocabulary is not found, no further action is taken.",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = VocabularyOutputSchema.class))),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Vocabulary was updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Vocabulary could not be updated, because it does not exist."),
                    @ApiResponse(responseCode = "400", description = "Request was malformed, e.g. id was null",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiCoreSchema.ApiErrorDetailSchema.class)))),
            })
    void updateVocabulary(JsonObject vocabulary);

    /**
     * Removes the {@link Vocabulary} with the given ID 
     *
     * @param id id of the vocabulary
     * @return JsonObject with the updated vocabulary
     */
    @Operation(description = "Removes a vocabulary with the given ID if possible",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Vocabulary was deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Request was malformed, e.g. id was null",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiCoreSchema.ApiErrorDetailSchema.class)))),
                    @ApiResponse(responseCode = "404", description = "Vocabulary could not be removed, because it does not exist.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiCoreSchema.ApiErrorDetailSchema.class))))
            })
    void removeVocabulary(String id);

    /**
     * Vocabulary output
     */
    @ArraySchema()
    @Schema(name = "VocabularyOutput", example = VocabularyOutputSchema.VOCABULARY_OUTPUT_EXAMPLE)
    record VocabularyOutputSchema(
            @Schema(name = ID)
            String id,
            @Schema(name = TYPE, example = EDC_VOCABULARY_TYPE)
            String name,
            String jsonSchema
    ) {
        public static final String VOCABULARY_OUTPUT_EXAMPLE = """
                {
                    "@id": "vocabularyId",
                    "name": "vocabulary name",
                    "jsonSchema":  "{ \\"title\\": \\"vocabulary\\", \\"type\\": \\"object\\", \\"properties\\": { \\"name\\": { \\"type\\": \\"string\\", \\"title\\": \\"Name\\" }, \\"dct:keyword\\": { \\"type\\": \\"array\\", \\"title\\": \\"Keywords\\", \\"items\\": { \\"type\\": \\"string\\" } } }, \\"required\\": [ \\"name\\" ], \\"@context\\": { \\"dct\\": \\"http:\\/\\/purl.org\\/dc\\/terms\\/\" } }",
                    "category": "dataset"
                }
                """;
    }

}
