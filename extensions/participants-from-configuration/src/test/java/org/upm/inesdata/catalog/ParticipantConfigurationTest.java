package org.upm.inesdata.catalog;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.eclipse.edc.crawler.spi.TargetNode;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.configuration.ConfigFactory;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class ParticipantConfigurationTest {

    private final Monitor monitor = mock();

    @Test
    public void testParticipantsRetrieval() {

        Map<String, String> properties = Map.ofEntries(
            entry("edc.catalog.configuration.participant.1.name", "connector-c1"),
            entry("edc.catalog.configuration.participant.1.id", "cnc1"),
            entry("edc.catalog.configuration.participant.1.targetUrl", "http://connector-c1.com"),
            entry("edc.catalog.configuration.participant.2.name", "connector-c2"),
            entry("edc.catalog.configuration.participant.2.id", "cnc2"),
            entry("edc.catalog.configuration.participant.2.targetUrl", "http://connector-c2.com"),
            entry("edc.catalog.configuration.participant.3.name", "connector-c3"),
            entry("edc.catalog.configuration.participant.3.id", "cnc3"),
            entry("edc.catalog.configuration.participant.3.targetUrl", "http://connector-c3.com"),
            entry("edc.catalog.configuration.specs", "inesdata connector"),
            entry("edc.catalog.configuration.version", "1.0.0"));

        ParticipantConfiguration partConfiguration = new ParticipantConfiguration(monitor);
        var targets = partConfiguration.getTargetNodes(ConfigFactory.fromMap(properties));

        assertThat(targets).containsAll(List.of(
            new TargetNode("connector-c1", "cnc1", "http://connector-c1.com", ParticipantConfiguration.SUPPORTED_PROTOCOLS),
            new TargetNode("connector-c2", "cnc2", "http://connector-c2.com", ParticipantConfiguration.SUPPORTED_PROTOCOLS),
            new TargetNode("connector-c3", "cnc3", "http://connector-c3.com", ParticipantConfiguration.SUPPORTED_PROTOCOLS)));

        assertThat(targets.size()).isEqualTo(3);
    }

    @Test
    void verify_noParticipants() {
        Map<String, String> properties = Map.ofEntries();
        ParticipantConfiguration partConfiguration = new ParticipantConfiguration(monitor);
        var targets = partConfiguration.getTargetNodes(ConfigFactory.fromMap(properties));

        assertThat(targets.size()).isEqualTo(0);
    }

    @Test
    void verify_wrongParticipantListFormat() {
        Map<String, String> properties = Map.ofEntries(
            entry("edc.catalog.configuration.participant.1.name", "connector-c1"),
            entry("edc.catalog.configuration.participant.1.id", "cnc1"));

        ParticipantConfiguration partConfiguration = new ParticipantConfiguration(monitor);
        var targets = partConfiguration.getTargetNodes(ConfigFactory.fromMap(properties));

        assertThat(targets.get(targets.size() -1)).isNull();
    }
    
}
