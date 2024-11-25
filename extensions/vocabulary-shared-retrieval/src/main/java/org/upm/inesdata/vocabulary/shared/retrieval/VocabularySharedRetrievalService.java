package org.upm.inesdata.vocabulary.shared.retrieval;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.system.configuration.Config;
import org.upm.inesdata.catalog.ParticipantRegistrationService;
import org.upm.inesdata.spi.vocabulary.VocabularySharedService;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;

import java.util.List;

/**
 * Service for making a request for getting vocabularies from other connectors
 */
public class VocabularySharedRetrievalService {

    private final VocabularySharedService vocabularySharedService;

    private final Monitor monitor;

    private final ParticipantRegistrationService participantRegistrationService;

    private final Client client = ClientBuilder.newClient();

    /**
     * Public constructor
     *
     * @param vocabularySharedService        the vocabularySharedService
     * @param monitor                        the monitor
     * @param participantRegistrationService the participationRegistrationService
     */
    public VocabularySharedRetrievalService(VocabularySharedService vocabularySharedService, Monitor monitor, ParticipantRegistrationService participantRegistrationService) {
        this.vocabularySharedService = vocabularySharedService;
        this.monitor = monitor;
        this.participantRegistrationService = participantRegistrationService;
    }

    /**
     * Retrieves the v
     *
     * @param config                    the context config
     * @param tokenRepresentationResult the token
     * @param participantId
     */
    public void retrieveVocabularies(Config config, Result<TokenRepresentation> tokenRepresentationResult,
        String participantId) {
        participantRegistrationService.getSharedUrlParticipantNodes(config, tokenRepresentationResult).stream().filter(s-> !participantId.equals(s.getParticipantId())).forEach(sharedUrlParticipant -> {
            try {
                String response = makeHttpPostRequest(sharedUrlParticipant.getParticipantId(), sharedUrlParticipant.getSharedUrl(), tokenRepresentationResult);
                vocabularySharedService.deleteVocabulariesByConnectorId(sharedUrlParticipant.getParticipantId());
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                List<Vocabulary> vocabularies = objectMapper.readValue(response, new TypeReference<>() {
                });
                List<JsonNode> nodes = objectMapper.readValue(response, new TypeReference<>() {
                });
                for(int i=0; i< vocabularies.size(); i++){
                    Vocabulary vocabulary = vocabularies.get(i).toBuilder().id(nodes.get(i).get("@id").asText()).build();
                    vocabularySharedService.create(vocabulary);
                }
            } catch (Exception e) {
                monitor.severe("Exception occurred while making HTTP POST request: " + e.getMessage());
            }
        });
    }

    private String makeHttpPostRequest(String participantId, String url, Result<TokenRepresentation> tokenRepresentationResult) {
        String token = tokenRepresentationResult.getContent().getToken();
        String fullUrl = url+"/connector-vocabularies/request-by-connector";
        WebTarget target = client.target(fullUrl);

        JsonObject jsonBody = Json.createObjectBuilder()
                .add("@context", Json.createObjectBuilder()
                        .add("@vocab", "https://w3id.org/edc/v0.0.1/ns/"))
                .add("connectorId", participantId)
                .build();


        return target.request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .post(Entity.json(jsonBody.toString()), String.class);
    }
}
