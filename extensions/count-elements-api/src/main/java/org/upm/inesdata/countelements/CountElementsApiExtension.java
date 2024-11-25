package org.upm.inesdata.countelements;

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
import org.eclipse.edc.web.spi.configuration.ApiContext;
import org.upm.inesdata.countelements.controller.CountElementsApiController;
import org.upm.inesdata.countelements.service.CountElementsServiceImpl;
import org.upm.inesdata.countelements.transformer.JsonObjectFromCountElementTransformer;
import org.upm.inesdata.spi.countelements.index.CountElementsIndex;
import org.upm.inesdata.spi.countelements.service.CountElementsService;

import java.util.Map;

import static org.eclipse.edc.spi.constants.CoreConstants.JSON_LD;

/**
 * Extension that provides an API for getting the total number of elements of an entity
 */
@Extension(value = CountElementsApiExtension.NAME)
public class CountElementsApiExtension implements ServiceExtension {

    public static final String NAME = "CountElement Elements API Extension";

    @Inject
    private WebService webService;

    @Inject
    private TypeTransformerRegistry transformerRegistry;

    @Inject(required = false)
    private HealthCheckService healthCheckService;

    @Inject
    private TypeManager typeManager;

    @Inject
    private CountElementsIndex countElementsIndex;

    @Inject
    private TransactionContext transactionContext;

    @Inject
    private JsonObjectValidatorRegistry validator;

    @Override
    public String name() {
        return NAME;
    }

    /**
     * Provides a default countElementsService implementation
     */
    @Provider(isDefault = true)
    public CountElementsService countElementsService() {
        return new CountElementsServiceImpl(countElementsIndex, transactionContext);
    }

    /**
     * Initializes the service
     */
    @Override
    public void initialize(ServiceExtensionContext context) {
        var factory = Json.createBuilderFactory(Map.of());
        var jsonLdMapper = typeManager.getMapper(JSON_LD);
        var managementApiTransformerRegistry = transformerRegistry.forContext("management-api");
        managementApiTransformerRegistry.register(new JsonObjectFromCountElementTransformer(factory, jsonLdMapper));

        var countElementsApiController = new CountElementsApiController(countElementsService(), managementApiTransformerRegistry, validator);
        webService.registerResource(ApiContext.MANAGEMENT, countElementsApiController);
    }
}
