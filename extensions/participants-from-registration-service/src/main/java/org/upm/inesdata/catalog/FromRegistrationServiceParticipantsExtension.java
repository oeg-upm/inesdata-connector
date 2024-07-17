package org.upm.inesdata.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.catalog.directory.InMemoryNodeDirectory;
import org.eclipse.edc.crawler.spi.TargetNodeDirectory;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenParameters;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.eclipse.edc.catalog.spi.CacheSettings.DEFAULT_EXECUTION_PERIOD_SECONDS;

public class FromRegistrationServiceParticipantsExtension implements ServiceExtension {

    @Setting("The time to elapse between two crawl runs")
    public static final String EXECUTION_PLAN_PERIOD_SECONDS = "edc.participants.cache.execution.period.seconds";

    @Inject
    private IdentityService identityService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private InMemoryNodeDirectory nodeDirectory;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var periodSeconds = context.getSetting(EXECUTION_PLAN_PERIOD_SECONDS, DEFAULT_EXECUTION_PERIOD_SECONDS);
        var monitor = context.getMonitor();
        var participantConfig = new ParticipantConfiguration(monitor, new ObjectMapper());

        nodeDirectory = new InMemoryNodeDirectory(); // Initialize the directory

        scheduler.scheduleAtFixedRate(() -> {
            try {
                updateTargetNodeDirectory(context, participantConfig);
            } catch (Exception e) {
                monitor.severe("Error updating TargetNodeDirectory", e);
            }
        }, 0, periodSeconds, TimeUnit.SECONDS);
    }

    private void updateTargetNodeDirectory(ServiceExtensionContext context, ParticipantConfiguration participantConfig) {
        var newDir = new InMemoryNodeDirectory();

        Result<TokenRepresentation> tokenRepresentationResult = identityService.obtainClientCredentials(
            TokenParameters.Builder.newInstance().build());

        for (var target : participantConfig.getTargetNodes(context.getConfig(), tokenRepresentationResult)) {
            // skipping null target nodes
            if (target != null){
                newDir.insert(target);
            }
        }

        updateDirectoryInContext(newDir);
    }

    private synchronized void updateDirectoryInContext(InMemoryNodeDirectory newDir) {
        this.nodeDirectory = newDir;
    }

    @Provider
    public synchronized TargetNodeDirectory federatedCacheNodeDirectory(ServiceExtensionContext context) {
        // Return the updated directory
        return nodeDirectory;
    }

    @Override
    public void shutdown() {
        scheduler.shutdown();
    }
}
