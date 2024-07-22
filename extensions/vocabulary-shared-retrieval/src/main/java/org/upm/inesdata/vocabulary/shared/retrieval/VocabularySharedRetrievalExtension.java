package org.upm.inesdata.vocabulary.shared.retrieval;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.api.auth.spi.AuthenticationRequestFilter;
import org.eclipse.edc.api.auth.spi.registry.ApiAuthenticationRegistry;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.runtime.metamodel.annotation.SettingContext;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenParameters;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.system.ExecutorInstrumentation;
import org.eclipse.edc.spi.system.Hostname;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.web.jersey.providers.jsonld.JerseyJsonLdInterceptor;
import org.eclipse.edc.web.jersey.providers.jsonld.ObjectMapperProvider;
import org.eclipse.edc.web.spi.WebServer;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.WebServiceConfiguration;
import org.eclipse.edc.web.spi.configuration.WebServiceConfigurer;
import org.eclipse.edc.web.spi.configuration.WebServiceSettings;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static org.eclipse.edc.policy.model.OdrlNamespace.ODRL_PREFIX;
import static org.eclipse.edc.policy.model.OdrlNamespace.ODRL_SCHEMA;
import static org.eclipse.edc.spi.constants.CoreConstants.JSON_LD;

/**
 * This extension launch a task to retrieve the vocabularies from other connectors .
 */
@Extension(value = VocabularySharedRetrievalExtension.NAME)
public class VocabularySharedRetrievalExtension implements ServiceExtension {
    public static final String NAME = "Vocabulary Shared Retrieval Extension";

    @Setting("The time to elapse between two crawl runs")
    public static final String EXECUTION_PLAN_PERIOD_SECONDS = "edc.participants.cache.execution.period.seconds";

    private final int DEFAULT_EXECUTION_PERIOD_SECONDS = 60;

    @Inject
    private IdentityService identityService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void initialize(ServiceExtensionContext context) {
        var periodSeconds = context.getSetting(EXECUTION_PLAN_PERIOD_SECONDS, DEFAULT_EXECUTION_PERIOD_SECONDS);
        var monitor = context.getMonitor();
        //var participantRegistrationService = new ParticipantRegistrationService(monitor, new ObjectMapper());

        // Schedule periodic updates
        scheduler.scheduleAtFixedRate(() -> {
            try {
               // updateTargetNodeDirectory(context, participantRegistrationService);
            } catch (Exception e) {
                monitor.severe("Error updating TargetNodeDirectory", e);
            }
        }, periodSeconds, periodSeconds, TimeUnit.SECONDS);
    }

    /*private void retrieveVocabularies(ServiceExtensionContext context, ParticipantRegistrationService participantRegistrationService) {

        Result<TokenRepresentation> tokenRepresentationResult = identityService.obtainClientCredentials(
                TokenParameters.Builder.newInstance().build());

        for (var target : participantRegistrationService.getTargetNodes(context.getConfig(), tokenRepresentationResult)) {
            // skipping null target nodes
            if (target != null){
                newDir.insert(target);
            }
        }

    }*/
}
