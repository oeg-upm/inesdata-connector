package org.upm.inesdata.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.catalog.directory.InMemoryNodeDirectory;
import org.eclipse.edc.crawler.spi.TargetNodeDirectory;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenParameters;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;


/**
 * Extension to set up federated target node directory using a configuration variable.
 */
public class FromRegistrationServiceParticipantsExtension implements ServiceExtension {

    @Inject
    private IdentityService identityService;

    @Provider
    public TargetNodeDirectory federatedCacheNodeDirectory(ServiceExtensionContext context) {

        var monitor = context.getMonitor();
        var participantConfig = new ParticipantConfiguration(monitor, new ObjectMapper());

        var dir = new InMemoryNodeDirectory();

        Result<TokenRepresentation> tokenRepresentationResult = identityService.obtainClientCredentials(
            TokenParameters.Builder.newInstance().build());
        for (var target : participantConfig.getTargetNodes(context.getConfig(), tokenRepresentationResult)) {
            // skipping null target nodes
            if (target != null){
                dir.insert(target);
            }
        }
        return dir;
    }
}
