package org.upm.inesdata.federated;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import org.eclipse.edc.connector.controlplane.transform.edc.from.JsonObjectFromAssetTransformer;
import org.eclipse.edc.connector.controlplane.transform.edc.to.JsonObjectToAssetTransformer;
import org.eclipse.edc.connector.controlplane.transform.odrl.OdrlTransformersFactory;
import org.eclipse.edc.connector.controlplane.transform.odrl.to.JsonObjectToPolicyTransformer;
import org.eclipse.edc.connector.core.agent.NoOpParticipantIdMapper;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.ApiContext;
import org.upm.inesdata.complexpolicy.mapper.AtomicConstraintMapper;
import org.upm.inesdata.complexpolicy.mapper.ExpressionExtractor;
import org.upm.inesdata.complexpolicy.mapper.ExpressionMapper;
import org.upm.inesdata.complexpolicy.mapper.LiteralMapper;
import org.upm.inesdata.complexpolicy.mapper.OperatorMapper;
import org.upm.inesdata.complexpolicy.mapper.PolicyMapper;
import org.upm.inesdata.complexpolicy.mapper.PolicyValidator;
import org.upm.inesdata.federated.controller.FederatedCatalogCacheApiController;
import org.upm.inesdata.federated.service.FederatedCatalogCacheServiceImpl;
import org.upm.inesdata.federated.transformer.JsonObjectFromUiContractOfferTransformer;
import org.upm.inesdata.spi.federated.FederatedCatalogCacheService;
import org.upm.inesdata.spi.federated.index.PaginatedFederatedCacheStoreIndex;

import java.util.Map;

import static org.eclipse.edc.spi.constants.CoreConstants.JSON_LD;
/**
 * Extension that provides an API for managing vocabularies
 */
@Extension(value = FederatedCatalogCacheApiExtension.NAME)
public class FederatedCatalogCacheApiExtension implements ServiceExtension {

    public static final String NAME = "StorageAsset API Extension";
    @Inject
    private WebService webService;

    @Inject
    private TypeManager typeManager;
    
    @Inject
    private TransactionContext transactionContext;

    @Inject
    private TypeTransformerRegistry transformerRegistry;

    @Inject
    private JsonObjectValidatorRegistry validator;
    @Inject
    private JsonLd jsonLd;

    @Inject
    private Vault vault;

    @Inject
    private PaginatedFederatedCacheStoreIndex paginatedFederatedCacheStoreIndex;

    @Override
    public String name() {
        return NAME;
    }
    /**
     * Provides a default federatedCatalogCacheService implementation
     */
    @Provider(isDefault = true)
    public FederatedCatalogCacheService federatedCatalogCacheService() {
        return new FederatedCatalogCacheServiceImpl(paginatedFederatedCacheStoreIndex,transactionContext);
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
        managementApiTransformerRegistry.register(new JsonObjectFromAssetTransformer(factory, jsonLdMapper));
        managementApiTransformerRegistry.register(new JsonObjectToAssetTransformer());

        var participantIdMapper = new NoOpParticipantIdMapper();
        managementApiTransformerRegistry.register(new JsonObjectToPolicyTransformer(participantIdMapper));
        managementApiTransformerRegistry.register(new JsonObjectFromUiContractOfferTransformer(jsonLdMapper, factory));
        OdrlTransformersFactory.jsonObjectToOdrlTransformers(participantIdMapper).forEach(managementApiTransformerRegistry::register);

        ExpressionMapper expressionMapper = new ExpressionMapper(
                new AtomicConstraintMapper(new LiteralMapper(new ObjectMapper()), new OperatorMapper()));
        ExpressionExtractor expressionExtractor = new ExpressionExtractor(new PolicyValidator(), expressionMapper);
        PolicyMapper policyMapper = new PolicyMapper(expressionExtractor, expressionMapper, managementApiTransformerRegistry);

        var federatedCatalogCacheApiController = new FederatedCatalogCacheApiController(this.federatedCatalogCacheService(), managementApiTransformerRegistry,
            validator, monitor, policyMapper);
        webService.registerResource(ApiContext.MANAGEMENT, federatedCatalogCacheApiController);
    }
}
