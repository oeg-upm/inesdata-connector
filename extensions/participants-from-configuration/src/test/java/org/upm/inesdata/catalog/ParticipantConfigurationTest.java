package org.upm.inesdata.catalog;

import static java.util.Map.entry;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.eclipse.edc.crawler.spi.TargetNode;
import org.eclipse.edc.spi.system.configuration.ConfigFactory;
import org.junit.Test; 

public class ParticipantConfigurationTest {

    @Test
    public void testParticipantsRetrieval() {

        Map<String, String> properties = Map.ofEntries(
            entry("edc.catalog.configuration.participant.1.name", "Paco"),
            entry("edc.catalog.configuration.participant.1.id", "paco"),
            entry("edc.catalog.configuration.participant.1.targetUrl", "http://paco.com"),
            entry("edc.catalog.configuration.participant.2.name", "Pepe"),
            entry("edc.catalog.configuration.participant.2.id", "pepe"),
            entry("edc.catalog.configuration.participant.2.targetUrl", "http://pepe.com"),
            entry("edc.catalog.configuration.participant.3.name", "Pedro"),
            entry("edc.catalog.configuration.participant.3.id", "pedro"),
            entry("edc.catalog.configuration.participant.3.targetUrl", "http://pedro.com"),
            entry("edc.catalog.configuration.other", "Other 1"),
            entry("edc.catalog.configuration.mother", "Other Mother"));

        ParticipantConfiguration partConfiguration = new ParticipantConfiguration();
        var targets = partConfiguration.getTargetNodes(ConfigFactory.fromMap(properties));

        assertTrue(targets.containsAll(List.of(
            new TargetNode("Paco", "paco", "http://paco.com", ParticipantConfiguration.SUPPORTED_PROTOCOLS),
            new TargetNode("Pepe", "pepe", "http://pepe.com", ParticipantConfiguration.SUPPORTED_PROTOCOLS),
            new TargetNode("Pedro", "pedro", "http://pedro.com", ParticipantConfiguration.SUPPORTED_PROTOCOLS))));

    }
    
}
