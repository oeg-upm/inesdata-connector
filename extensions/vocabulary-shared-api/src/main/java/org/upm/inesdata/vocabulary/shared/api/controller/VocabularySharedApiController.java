package org.upm.inesdata.vocabulary.shared.api.controller;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.upm.inesdata.spi.vocabulary.VocabularySharedService;
import org.upm.inesdata.spi.vocabulary.domain.ConnectorVocabulary;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;

import static jakarta.json.stream.JsonCollectors.toJsonArray;

/**
 * Implementation of the controller for getting {@link Vocabulary}.
 */
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("/connector-vocabularies")
public class VocabularySharedApiController implements VocabularySharedApi {

    private final TypeTransformerRegistry transformerRegistry;
    private final VocabularySharedService vocabularySharedService;
    private final Monitor monitor;

    /**
     * Constructor
     */
    public VocabularySharedApiController(TypeTransformerRegistry transformerRegistry, VocabularySharedService vocabularySharedService, Monitor monitor) {
        this.transformerRegistry = transformerRegistry;
        this.vocabularySharedService = vocabularySharedService;
        this.monitor = monitor;
    }

    @POST
    @Path("/request-by-connector")
    @Override
    public JsonArray getVocabulariesFromConnector(JsonObject connectorVocabularyJson) {
        var connectorVocabulary = transformerRegistry.transform(connectorVocabularyJson, ConnectorVocabulary.class)
                .orElseThrow(InvalidRequestException::new);

        return vocabularySharedService.searchVocabulariesByConnector(connectorVocabulary).getContent().stream()
                .map(it -> transformerRegistry.transform(it, JsonObject.class))
                .peek(r -> r.onFailure(f -> monitor.warning(f.getFailureDetail())))
                .filter(Result::succeeded)
                .map(Result::getContent)
                .collect(toJsonArray());
    }

    @POST
    @Path("/request")
    @Override
    public JsonArray getVocabularies() {
        return vocabularySharedService.search().getContent().stream()
                .map(it -> transformerRegistry.transform(it, JsonObject.class))
                .peek(r -> r.onFailure(f -> monitor.warning(f.getFailureDetail())))
                .filter(Result::succeeded)
                .map(Result::getContent)
                .collect(toJsonArray());
    }
}
