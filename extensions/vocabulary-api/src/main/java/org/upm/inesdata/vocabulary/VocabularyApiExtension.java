package org.upm.inesdata.vocabulary;

import jakarta.json.Json;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.health.HealthCheckResult;
import org.eclipse.edc.spi.system.health.HealthCheckService;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.ApiContext;
import org.upm.inesdata.spi.vocabulary.VocabularyIndex;
import org.upm.inesdata.spi.vocabulary.VocabularyService;
import org.upm.inesdata.vocabulary.controller.VocabularyApiController;
import org.upm.inesdata.vocabulary.service.VocabularyServiceImpl;
import org.upm.inesdata.vocabulary.storage.InMemoryVocabularyIndex;
import org.upm.inesdata.vocabulary.transformer.JsonObjectFromVocabularyTransformer;
import org.upm.inesdata.vocabulary.transformer.JsonObjectToVocabularyTransformer;
import org.upm.inesdata.vocabulary.validator.VocabularyValidator;

import java.util.Map;

import static org.eclipse.edc.spi.constants.CoreConstants.JSON_LD;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.EDC_VOCABULARY_TYPE;

/**
 * Extension that provides an API for managing vocabularies
 */
@Extension(value = VocabularyApiExtension.NAME)
public class VocabularyApiExtension implements ServiceExtension {

    public static final String NAME = "Vocabulary API Extension";
    private InMemoryVocabularyIndex defaultVocabularyIndex;

    @Inject
    private WebService webService;

    @Inject
    private VocabularyIndex vocabularyIndex;

    @Inject(required = false)
    private HealthCheckService healthCheckService;

    @Inject
    private TypeManager typeManager;
    
    @Inject
    private TransactionContext transactionContext;

    @Inject
    private TypeTransformerRegistry transformerRegistry;

    @Inject
    private JsonObjectValidatorRegistry validator;

    @Override
    public String name() {
        return NAME;
    }

    /**
     * Provides a default vocabularyService implementation
     */
    @Provider(isDefault = true)
    public VocabularyService vocabularyService() {
        return new VocabularyServiceImpl(vocabularyIndex, transactionContext);
    }

    /**
     * Initializes the service
     */
    @Override
    public void initialize(ServiceExtensionContext context) {
        var monitor = context.getMonitor();

        var managementApiTransformerRegistry = transformerRegistry.forContext("management-api");

        var factory = Json.createBuilderFactory(Map.of());
        var jsonLdMapper = typeManager.getMapper(JSON_LD);
        managementApiTransformerRegistry.register(new JsonObjectFromVocabularyTransformer(factory, jsonLdMapper));
        managementApiTransformerRegistry.register(new JsonObjectToVocabularyTransformer());

        validator.register(EDC_VOCABULARY_TYPE, VocabularyValidator.instance());
        var vocabularyApiController = new VocabularyApiController(this.vocabularyService(), managementApiTransformerRegistry, monitor, validator, context.getParticipantId());
        webService.registerResource(ApiContext.MANAGEMENT, vocabularyApiController);

        // contribute to the liveness probe
        if (healthCheckService != null) {
            var successResult = HealthCheckResult.Builder.newInstance().component("FCC Query API").build();
            healthCheckService.addReadinessProvider(() -> successResult);
            healthCheckService.addLivenessProvider(() -> successResult);
        }
    }
}
