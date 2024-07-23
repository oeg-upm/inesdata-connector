package org.upm.inesdata.vocabulary.shared.retrieval;

import org.eclipse.edc.crawler.spi.TargetNode;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.system.configuration.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles participants in configuration and transforms them into TargetNodes
 */
public class SharedUrlRegistrationService {

    @Setting
    public static final String EDC_CATALOG_REGISTRATION_SERVICE_HOST = "edc.catalog.registration.service.host";
    public static final String RESOURCE_URL = "/public/participants";

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
}
