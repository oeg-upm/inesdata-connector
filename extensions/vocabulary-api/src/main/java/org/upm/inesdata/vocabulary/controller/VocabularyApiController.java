package org.upm.inesdata.vocabulary.controller;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.eclipse.edc.web.spi.exception.ObjectNotFoundException;
import org.eclipse.edc.web.spi.exception.ValidationFailureException;
import org.upm.inesdata.spi.vocabulary.VocabularyService;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;

import static jakarta.json.stream.JsonCollectors.toJsonArray;
import static java.util.Optional.of;
import static org.eclipse.edc.web.spi.exception.ServiceResultHandler.exceptionMapper;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.EDC_VOCABULARY_TYPE;

import org.eclipse.edc.api.model.IdResponse;

/**
 * Implementation of the controller for {@link Vocabulary} managing.
 */
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("/vocabularies")
public class VocabularyApiController implements VocabularyApi {
    private final TypeTransformerRegistry transformerRegistry;
    private final VocabularyService service;
    private final Monitor monitor;
    private final JsonObjectValidatorRegistry validator;

    /**
     * Constructor
     */
    public VocabularyApiController(VocabularyService service, TypeTransformerRegistry transformerRegistry,
                              Monitor monitor, JsonObjectValidatorRegistry validator) {
        this.transformerRegistry = transformerRegistry;
        this.service = service;
        this.monitor = monitor;
        this.validator = validator;
    }

    @POST
    @Override
    public JsonObject createVocabulary(JsonObject vocabularyJson) {
        validator.validate(EDC_VOCABULARY_TYPE, vocabularyJson).orElseThrow(ValidationFailureException::new);

        var vocabulary = transformerRegistry.transform(vocabularyJson, Vocabulary.class)
                .orElseThrow(InvalidRequestException::new);

        var idResponse = service.create(vocabulary)
                .map(a -> IdResponse.Builder.newInstance()
                        .id(a.getId())
                        .createdAt(a.getCreatedAt())
                        .build())
                .orElseThrow(exceptionMapper(Vocabulary.class, vocabulary.getId()));

        return transformerRegistry.transform(idResponse, JsonObject.class)
                .orElseThrow(f -> new EdcException(f.getFailureDetail()));
    }

    @POST
    @Path("/request")
    @Override
    public JsonArray getVocabularies() {
        return service.search().getContent().stream()
        .map(it -> transformerRegistry.transform(it, JsonObject.class))
        .peek(r -> r.onFailure(f -> monitor.warning(f.getFailureDetail())))
        .filter(Result::succeeded)
        .map(Result::getContent)
        .collect(toJsonArray());
    }

    @GET
    @Path("{id}")
    @Override
    public JsonObject getVocabulary(@PathParam("id") String id) {
        var vocabulary = of(id)
                .map(it -> service.findById(id))
                .orElseThrow(() -> new ObjectNotFoundException(Vocabulary.class, id));

        return transformerRegistry.transform(vocabulary, JsonObject.class)
                .orElseThrow(f -> new EdcException(f.getFailureDetail()));

    }

    @DELETE
    @Path("{id}")
    @Override
    public void removeVocabulary(@PathParam("id") String id) {
        service.delete(id).orElseThrow(exceptionMapper(Vocabulary.class, id));
    }

    @PUT
    @Override
    public void updateVocabulary(JsonObject vocabularyJson) {
        validator.validate(EDC_VOCABULARY_TYPE, vocabularyJson).orElseThrow(ValidationFailureException::new);
        
        var vocabularyResult = transformerRegistry.transform(vocabularyJson, Vocabulary.class)
                .orElseThrow(InvalidRequestException::new);

        service.update(vocabularyResult)
                .orElseThrow(exceptionMapper(Vocabulary.class, vocabularyResult.getId()));
    }

}
