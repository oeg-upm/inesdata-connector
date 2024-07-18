package org.upm.inesdata.catalog;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.monitor.Monitor;

import org.eclipse.edc.crawler.spi.TargetNode;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.system.configuration.Config;

import static java.lang.String.format;

/**
 * Handles participants in configuration and transforms them into TargetNodes
 */
public class ParticipantRegistrationService {

    public static final List<String> SUPPORTED_PROTOCOLS = List.of("dataspace-protocol-http");

    @Setting
    public static final String EDC_CATALOG_REGISTRATION_SERVICE_HOST = "edc.catalog.registration.service.host";
    public static final String RESOURCE_URL = "/public/participants";

    private final Monitor monitor;
    private final Client client = ClientBuilder.newClient();
    private final ObjectMapper objectMapper;


    /**
     * Constructor
     *
     * @param monitor      monitor
     * @param objectMapper mapper
     */
    public ParticipantRegistrationService(Monitor monitor, ObjectMapper objectMapper) {
        this.monitor = monitor;
        this.objectMapper = objectMapper;
    }




    /**
     * Makes an HTTP GET request to the specified URL and returns the response as a string.
     *
     * @param url                       the URL to make the GET request to
     * @param tokenRepresentationResult token
     * @return the response from the GET request
     */
    public String makeHttpGetRequest(String url, Result<TokenRepresentation> tokenRepresentationResult) {
        String token = tokenRepresentationResult.getContent().getToken();
        WebTarget target = client.target(url);
        return target.request(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token).get(String.class);
    }

    /**
     * Retrieve TargetNodes from configuration
     *
     * @param baseConfig                EDC Configuration
     * @param tokenRepresentationResult token
     * @return list of TargetNodes from configuration
     */
    public List<TargetNode> getTargetNodes(Config baseConfig, Result<TokenRepresentation> tokenRepresentationResult) {
        var participantsConfig = baseConfig.getConfig(EDC_CATALOG_REGISTRATION_SERVICE_HOST);

        if (participantsConfig.getEntries().isEmpty()) {
            monitor.severe("Error processing url registration service.");
            return new ArrayList<>();
        } else {
            var url = participantsConfig.getEntries().get(EDC_CATALOG_REGISTRATION_SERVICE_HOST) + RESOURCE_URL;

            try {
                String response = makeHttpGetRequest(url, tokenRepresentationResult);
                if(response==null){
                    return new ArrayList<>();
                }
                // Process the response and convert it to TargetNodes
                // Assuming a method processResponseToTargetNodes(response)
                return processResponseToTargetNodes(response);
            } catch (Exception e) {
                monitor.severe("Exception occurred while making HTTP GET request: " + e.getMessage());
                return new ArrayList<>();
            }
        }
    }

    private List<TargetNode> processResponseToTargetNodes(String response) {
        List<TargetNode> targetNodes = new ArrayList<>();

        try {
            List<JsonNode> nodes = objectMapper.readValue(response, new TypeReference<>() {});

            for (JsonNode node : nodes) {
                String participantId = node.get("participantId").asText();
                String url = node.get("url").asText();
                TargetNode targetNode = new TargetNode(participantId, participantId, url, SUPPORTED_PROTOCOLS);
                targetNodes.add(targetNode);
            }
        } catch (Exception e) {
            monitor.severe("Failed to deserialize the registration service response");
        }

        return targetNodes;
    }

}
