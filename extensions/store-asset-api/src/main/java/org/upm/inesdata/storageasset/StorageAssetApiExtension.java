package org.upm.inesdata.storageasset;

import jakarta.json.Json;
import org.eclipse.edc.api.validation.DataAddressValidator;
import org.eclipse.edc.connector.api.management.configuration.ManagementApiConfiguration;
import org.eclipse.edc.connector.controlplane.api.management.asset.validation.AssetValidator;
import org.eclipse.edc.connector.controlplane.services.spi.asset.AssetService;
import org.eclipse.edc.connector.controlplane.transform.edc.from.JsonObjectFromAssetTransformer;
import org.eclipse.edc.connector.controlplane.transform.edc.to.JsonObjectToAssetTransformer;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.WebService;
import org.upm.inesdata.storageasset.controller.StorageAssetApiController;
import org.upm.inesdata.storageasset.service.S3Service;
import software.amazon.awssdk.regions.Region;

import java.util.Map;

import static org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset.EDC_ASSET_TYPE;
import static org.eclipse.edc.spi.constants.CoreConstants.JSON_LD;
import static org.eclipse.edc.spi.types.domain.DataAddress.EDC_DATA_ADDRESS_TYPE;
/**
 * Extension that provides an API for managing vocabularies
 */
@Extension(value = StorageAssetApiExtension.NAME)
public class StorageAssetApiExtension implements ServiceExtension {

    public static final String NAME = "StorageAsset API Extension";
    
    @Inject
    private AssetService assetService;
    
    @Inject
    private WebService webService;

    @Inject
    private ManagementApiConfiguration config;

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


    @Override
    public String name() {
        return NAME;
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

        validator.register(EDC_ASSET_TYPE, AssetValidator.instance());
        validator.register(EDC_DATA_ADDRESS_TYPE, DataAddressValidator.instance());

        // Leer las variables de entorno
        String accessKey = context.getSetting("edc.aws.access.key","");
        String secretKey = context.getSetting("edc.aws.secret.access.key","");
        String endpointOverride = context.getSetting("edc.aws.endpoint.override","");
        String regionName = context.getSetting("edc.aws.region","");
        String bucketName = context.getSetting("edc.aws.bucket.name","");

        Region region = Region.of(regionName);

        // Crear una instancia de S3Service
        S3Service s3Service = new S3Service(accessKey, secretKey, endpointOverride, region, bucketName);

        var storageAssetApiController = new StorageAssetApiController(assetService, managementApiTransformerRegistry,
            validator,s3Service,
            jsonLd, bucketName, regionName);
        webService.registerResource(config.getContextAlias(), storageAssetApiController);
    }
}
