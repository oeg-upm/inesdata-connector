package org.upm.inesdata.vocabulary.shared.retrieval;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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


    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    @Override
    public void initialize(ServiceExtensionContext context) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ParticipantRegistrationService participantRegistrationService = new ParticipantRegistrationService(context.getMonitor(), objectMapper);
        var periodSeconds = context.getSetting(EXECUTION_PLAN_PERIOD_SECONDS, DEFAULT_EXECUTION_PERIOD_SECONDS);
        VocabularySharedRetrievalService vocabularySharedRetrievalService = new VocabularySharedRetrievalService(vocabularySharedService, context.getMonitor(), participantRegistrationService);

        retrieveVocabularies(vocabularySharedRetrievalService, context);
        // Schedule periodic updates
        scheduler.scheduleAtFixedRate(() -> {
            retrieveVocabularies(vocabularySharedRetrievalService, context);
        }, periodSeconds, periodSeconds, TimeUnit.SECONDS);
    }

    private void retrieveVocabularies(VocabularySharedRetrievalService vocabularySharedRetrievalService, ServiceExtensionContext context) {
        try {
            Result<TokenRepresentation> tokenRepresentationResult = identityService.obtainClientCredentials(
                    TokenParameters.Builder.newInstance().build());
            vocabularySharedRetrievalService.retrieveVocabularies(context.getConfig(), tokenRepresentationResult, context.getParticipantId());
        } catch (Exception e) {
            context.getMonitor().severe("Error getting vocabularies from connectors", e);
        }

    }
}
