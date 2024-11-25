package org.upm.inesdata.catalog;

import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.edc.spi.monitor.Monitor;

import org.eclipse.edc.crawler.spi.TargetNode;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.configuration.Config;

import static java.lang.String.format;

/**
 * Handles participants in configuration and transforms them into TargetNodes
 */
public class ParticipantConfiguration  {

    public static final List<String> SUPPORTED_PROTOCOLS = List.of("dataspace-protocol-http");

    @Setting
    public static final String INESDATA_FC_PARTICIPANT_LIST = "edc.catalog.configuration.participant";

    private final Monitor monitor;

    /**
     * Constructs {@link ConfigurationTargetNodeDirectory}
     *
     * @param monitor   monitor
     * @param rawParticipantList RegistrationService API client.
     */
    public ParticipantConfiguration(Monitor monitor) {
        this.monitor = monitor;
    }

    /**
     * Retrieve TargetNodes from configuration
     *
     * @param baseConfig   EDC Configuration
     * @return list of TargetNodes from configuration
     */
    public List<TargetNode> getTargetNodes(Config baseConfig) {
        var participantsConfig = baseConfig.getConfig(INESDATA_FC_PARTICIPANT_LIST);

        // A partition returns a stream of all the groups formed by the properties sharing the next fragment in the key
        return participantsConfig.partition().map(conf -> toTargetNode(conf))
            .collect(Collectors.toList());

    }

    private TargetNode toTargetNode(Config participantConf) {
        TargetNode targetNode = null;

        if (! participantConf.getString("name", "").isEmpty() && 
                ! participantConf.getString("id", "").isEmpty() &&
                ! participantConf.getString("targetUrl", "").isEmpty()) {
            targetNode = new TargetNode(
                participantConf.getString("name"),
                participantConf.getString("id"),
                participantConf.getString("targetUrl"),
                SUPPORTED_PROTOCOLS );
        } else {
            monitor.severe(format("Error processing participant, this participant is skipped. A participant must contain non empty values for id, name and targetUrl. Provided values are id '%s', name '%s' and targetUrl '%s'.", 
                                  participantConf.getString("name", ""),
                                  participantConf.getString("id", ""),
                                  participantConf.getString("targetUrl", "")));
        }
        return targetNode;
    }
}
