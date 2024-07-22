package org.upm.inesdata.vocabulary.shared.api;

import jakarta.json.Json;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.health.HealthCheckService;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.WebService;
import org.upm.inesdata.spi.vocabulary.VocabularyIndex;
import org.upm.inesdata.spi.vocabulary.VocabularyService;
import org.upm.inesdata.spi.vocabulary.VocabularySharedService;
import org.upm.inesdata.vocabulary.service.VocabularyServiceImpl;
import org.upm.inesdata.vocabulary.shared.api.controller.VocabularySharedApi;
import org.upm.inesdata.vocabulary.shared.api.controller.VocabularySharedApiController;
import org.upm.inesdata.vocabulary.shared.api.service.VocabularySharedServiceImpl;
import org.upm.inesdata.vocabulary.shared.api.transformer.JsonObjectToConnectorVocabularyTransformer;
import org.upm.inesdata.vocabulary.transformer.JsonObjectFromVocabularyTransformer;
import org.upm.inesdata.vocabulary.transformer.JsonObjectToVocabularyTransformer;

import java.util.Map;

import static org.eclipse.edc.spi.constants.CoreConstants.JSON_LD;

/**
 * Extension that provides a shared API for getting vocabularies
 */
@Extension(value = VocabularySharedApiExtension.NAME)
public class VocabularySharedApiExtension implements ServiceExtension {
    public static final String NAME = "Vocabulary Shared API Extension";

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
    public VocabularySharedService vocabularySharedService() {
        return new VocabularySharedServiceImpl(vocabularyIndex, transactionContext);
    }

    /**
     * Initializes the service
     */
    @Override
    public void initialize(ServiceExtensionContext context) {
        var monitor = context.getMonitor();

        var sharedApiTransformerRegistry = transformerRegistry.forContext("shared-api");

        sharedApiTransformerRegistry.register(new JsonObjectToConnectorVocabularyTransformer());
        var factory = Json.createBuilderFactory(Map.of());
        var jsonLdMapper = typeManager.getMapper(JSON_LD);
        sharedApiTransformerRegistry.register(new JsonObjectFromVocabularyTransformer(factory, jsonLdMapper));

        var vocabularySharedApiController = new VocabularySharedApiController(sharedApiTransformerRegistry, this.vocabularySharedService(), monitor);
        webService.registerResource("shared", vocabularySharedApiController);

    }
}
