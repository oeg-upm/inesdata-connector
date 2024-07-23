package org.upm.inesdata.vocabulary.shared.retrieval;

import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenParameters;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.upm.inesdata.catalog.ParticipantRegistrationService;
import org.upm.inesdata.spi.vocabulary.VocabularySharedService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This extension launch a task to retrieve the vocabularies from other connectors .
 */
@Extension(value = VocabularySharedRetrievalExtension.NAME)
public class VocabularySharedRetrievalExtension implements ServiceExtension {
    public static final String NAME = "Vocabulary Shared Retrieval Extension";

    @Setting("The time to elapse between two crawl runs")
    public static final String EXECUTION_PLAN_PERIOD_SECONDS = "edc.vocabularies.task.execution.period.seconds";

    private final int DEFAULT_EXECUTION_PERIOD_SECONDS = 60;

    @Inject
    private IdentityService identityService;

    @Inject
    private VocabularySharedService vocabularySharedService;

    @Inject
    private ParticipantRegistrationService participantRegistrationService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);



    @Override
    public void initialize(ServiceExtensionContext context) {
        var periodSeconds = context.getSetting(EXECUTION_PLAN_PERIOD_SECONDS, DEFAULT_EXECUTION_PERIOD_SECONDS);
        var monitor = context.getMonitor();
        var vocabularySharedRetrievalService = new VocabularySharedRetrievalService(vocabularySharedService, monitor, participantRegistrationService);

        // Schedule periodic updates
        scheduler.scheduleAtFixedRate(() -> {
            try {
                Result<TokenRepresentation> tokenRepresentationResult = identityService.obtainClientCredentials(
                        TokenParameters.Builder.newInstance().build());
                vocabularySharedRetrievalService.retrieveVocabularies(context.getConfig(), tokenRepresentationResult);
            } catch (Exception e) {
                monitor.severe("Error getting vocabularies from connectors", e);
            }
        }, periodSeconds, periodSeconds, TimeUnit.SECONDS);
    }
}
