package org.upm.inesdata.catalog;

import org.eclipse.edc.catalog.directory.InMemoryNodeDirectory;
import org.eclipse.edc.crawler.spi.TargetNodeDirectory;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;


/**
 * Extension to set up federated target node directory using a configuration variable.
 */
public class FromConfigurationParticipantsExtension implements ServiceExtension {

    @Provider
    public TargetNodeDirectory federatedCacheNodeDirectory(ServiceExtensionContext context) {

        var participantConfig = new ParticipantConfiguration();

        // Maybe is much clerarer with a classic for loop
        // InMemoryNodeDirectory dir = participantConfig.getTargetNodes(context.getConfig()).stream()
        //     .collect(InMemoryNodeDirectory::new, (x, y) -> x.insert(y), null);

        var dir = new InMemoryNodeDirectory();
        for (var target : participantConfig.getTargetNodes(context.getConfig())) {
            dir.insert(target);
        }
        return dir;
    }
}
