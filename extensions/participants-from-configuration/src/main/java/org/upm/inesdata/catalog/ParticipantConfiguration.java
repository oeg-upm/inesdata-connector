package org.upm.inesdata.catalog;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.edc.crawler.spi.TargetNode;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.configuration.Config;

public class ParticipantConfiguration  {

    public static final List<String> SUPPORTED_PROTOCOLS = List.of("dataspace-protocol-http");

    @Setting
    public static final String INESDATA_FC_PARTICIPANT_LIST = "edc.catalog.configuration.participant";

    public List<TargetNode> getTargetNodes(Config baseConfig) {
        var participantsConfig = baseConfig.getConfig(INESDATA_FC_PARTICIPANT_LIST);

        // A partition returns a stream of all the groups formed by the properties sharing the next fragment in the key
        return participantsConfig.partition().map(conf -> toTargetNode(conf))
            .collect(Collectors.toList());

    }

    private TargetNode toTargetNode(Config participantConf) {
        return new TargetNode(
            participantConf.getString("name"),
            participantConf.getString("id"),
            participantConf.getString("targetUrl"), SUPPORTED_PROTOCOLS );
    }
}
