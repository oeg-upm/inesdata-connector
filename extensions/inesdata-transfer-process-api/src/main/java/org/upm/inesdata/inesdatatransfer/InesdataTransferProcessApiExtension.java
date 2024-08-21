package org.upm.inesdata.inesdatatransfer;

import jakarta.json.Json;
import jakarta.json.JsonBuilderFactory;
import org.eclipse.edc.connector.controlplane.api.management.transferprocess.transform.JsonObjectFromTransferProcessTransformer;
import org.eclipse.edc.connector.controlplane.api.management.transferprocess.transform.JsonObjectFromTransferStateTransformer;
import org.eclipse.edc.connector.controlplane.api.management.transferprocess.transform.JsonObjectToSuspendTransferTransformer;
import org.eclipse.edc.connector.controlplane.api.management.transferprocess.transform.JsonObjectToTerminateTransferTransformer;
import org.eclipse.edc.connector.controlplane.api.management.transferprocess.transform.JsonObjectToTransferRequestTransformer;
import org.eclipse.edc.connector.controlplane.api.management.transferprocess.validation.TerminateTransferValidator;
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.WebService;
import org.upm.inesdata.inesdatatransfer.controller.InesdataTransferProcessApiController;
import org.upm.inesdata.inesdatatransfer.validations.InesdataTransferRequestValidator;

import java.util.Collections;

@Extension("Management API: Inesdata Transfer Process")
public class InesdataTransferProcessApiExtension implements ServiceExtension {
    public static final String NAME = "Management API: Transfer Process";
    public static final String DEFAULT_VALUE = "";
    public static final String AWS_ACCESS_KEY = "edc.aws.access.key";
    public static final String AWS_SECRET_ACCESS = "edc.aws.secret.access.key";
    public static final String AWS_ENDPOINT_OVERRIDE = "edc.aws.endpoint.override";
    public static final String AWS_REGION = "edc.aws.region";
    public static final String AWS_BUCKET_NAME = "edc.aws.bucket.name";


    @Inject
    private WebService webService;
    @Inject
    private TypeTransformerRegistry transformerRegistry;
    @Inject
    private TransferProcessService service;
    @Inject
    private JsonObjectValidatorRegistry validatorRegistry;
    @Inject
    private Vault vault;

    public InesdataTransferProcessApiExtension() {
    }

    public String name() {
        return "Management API: Inesdata Transfer Process";
    }

    public void initialize(ServiceExtensionContext context) {
        JsonBuilderFactory builderFactory = Json.createBuilderFactory(Collections.emptyMap());
        TypeTransformerRegistry managementApiTransformerRegistry = this.transformerRegistry.forContext("management-api");
        managementApiTransformerRegistry.register(new JsonObjectFromTransferProcessTransformer(builderFactory));
        managementApiTransformerRegistry.register(new JsonObjectFromTransferStateTransformer(builderFactory));
        managementApiTransformerRegistry.register(new JsonObjectToTerminateTransferTransformer());
        managementApiTransformerRegistry.register(new JsonObjectToSuspendTransferTransformer());
        managementApiTransformerRegistry.register(new JsonObjectToTransferRequestTransformer());
        // Leer las variables de entorno
        var accessKey = vault.resolveSecret(context.getSetting(AWS_ACCESS_KEY, DEFAULT_VALUE));
        var secretKey = vault.resolveSecret(context.getSetting(AWS_SECRET_ACCESS, DEFAULT_VALUE));
        var endpointOverride = context.getSetting(AWS_ENDPOINT_OVERRIDE, DEFAULT_VALUE);
        var regionName = context.getSetting(AWS_REGION, DEFAULT_VALUE);
        var bucketName = context.getSetting(AWS_BUCKET_NAME, DEFAULT_VALUE);

        this.validatorRegistry.register("https://w3id.org/edc/v0.0.1/ns/TransferRequest", InesdataTransferRequestValidator.instance(context.getMonitor()));
        this.validatorRegistry.register("https://w3id.org/edc/v0.0.1/ns/TerminateTransfer", TerminateTransferValidator.instance());
        this.webService.registerResource("management", new InesdataTransferProcessApiController(context.getMonitor(), this.service, managementApiTransformerRegistry, this.validatorRegistry, bucketName, regionName, accessKey, secretKey, endpointOverride));
    }
}
