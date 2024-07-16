package org.upm.inesdata.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.catalog.directory.InMemoryNodeDirectory;
import org.eclipse.edc.crawler.spi.TargetNodeDirectory;
import org.eclipse.edc.crawler.spi.model.ExecutionPlan;
import org.eclipse.edc.crawler.spi.model.RecurringExecutionPlan;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenParameters;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import java.time.Duration;
import java.util.Random;

import static java.lang.String.format;
import static org.eclipse.edc.catalog.spi.CacheSettings.DEFAULT_EXECUTION_PERIOD_SECONDS;
import static org.eclipse.edc.catalog.spi.CacheSettings.DEFAULT_NUMBER_OF_CRAWLERS;
import static org.eclipse.edc.catalog.spi.CacheSettings.LOW_EXECUTION_PERIOD_SECONDS_THRESHOLD;

/**
 * Extension to set up federated target node directory using a configuration variable.
 */
public class FromRegistrationServiceParticipantsExtension implements ServiceExtension {

    @Setting("The time to elapse between two crawl runs")
    public static final String EXECUTION_PLAN_PERIOD_SECONDS = "edc.participants.cache.execution.period.seconds";
    @Setting("The number of crawlers (execution threads) that should be used. The engine will re-use crawlers when necessary.")
    public static final String NUM_CRAWLER_SETTING = "edc.participants.cache.partition.num.crawlers";
    @Setting("The initial delay for the cache crawler engine")
    public static final String EXECUTION_PLAN_DELAY_SECONDS = "edc.participants.cache.execution.delay.seconds";

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


    @Provider
    public ExecutionPlan createRecurringExecutionPlan(ServiceExtensionContext context) {
        var periodSeconds = context.getSetting(EXECUTION_PLAN_PERIOD_SECONDS, DEFAULT_EXECUTION_PERIOD_SECONDS);
        var setting = context.getSetting(EXECUTION_PLAN_DELAY_SECONDS, null);
        int initialDelaySeconds;
        if ("random".equals(setting) || setting == null) {
            initialDelaySeconds = randomSeconds();
        } else {
            try {
                initialDelaySeconds = Integer.parseInt(setting);
            } catch (NumberFormatException ex) {
                initialDelaySeconds = 0;
            }
        }
        var monitor = context.getMonitor();
        if (periodSeconds < LOW_EXECUTION_PERIOD_SECONDS_THRESHOLD) {
            var crawlers = context.getSetting(NUM_CRAWLER_SETTING, DEFAULT_NUMBER_OF_CRAWLERS);
            monitor.warning(format("An execution period of %d seconds is very low (threshold = %d). This might result in the work queue to be ever growing." +
                " A longer execution period or more crawler threads (currently using %d) should be considered.", periodSeconds, LOW_EXECUTION_PERIOD_SECONDS_THRESHOLD, crawlers));
        }
        return new RecurringExecutionPlan(Duration.ofSeconds(periodSeconds), Duration.ofSeconds(initialDelaySeconds), monitor);
    }

    private int randomSeconds() {
        var rnd = new Random();
        return 10 + rnd.nextInt(90);
    }
}
