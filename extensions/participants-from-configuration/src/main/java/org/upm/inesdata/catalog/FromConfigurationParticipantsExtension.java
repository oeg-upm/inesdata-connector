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

        var monitor = context.getMonitor();
        var participantConfig = new ParticipantConfiguration(monitor);

        var dir = new InMemoryNodeDirectory();
        for (var target : participantConfig.getTargetNodes(context.getConfig())) {
            // skipping null target nodes
            if (target != null){
                dir.insert(target);
            }
        }
        return dir;
    }
}
