package org.upm.inesdata.inesdatatransfer.controller;

import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.eclipse.edc.api.model.IdResponse;
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferRequest;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.constants.CoreConstants;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.eclipse.edc.web.spi.exception.ValidationFailureException;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferRequest.TRANSFER_REQUEST_TYPE;
import static org.eclipse.edc.web.spi.exception.ServiceResultHandler.mapToException;

@Consumes({ "application/json" })
@Produces({ "application/json" })
@Path("/v3/inesdatatransferprocesses")
public class InesdataTransferProcessApiController implements InesdataTransferProcessApi {

  protected final Monitor monitor;
  private final TransferProcessService service;
  private final TypeTransformerRegistry transformerRegistry;
  private final JsonObjectValidatorRegistry validatorRegistry;
  private final String bucketName;
  private final String region;
  private final String accessKey;
  private final String secretKey;
  private final String endpointOverride;

  public InesdataTransferProcessApiController(Monitor monitor, TransferProcessService service,
      TypeTransformerRegistry transformerRegistry, JsonObjectValidatorRegistry validatorRegistry, String bucketName,
      String region, String accessKey, String secretKey, String endpointOverride) {
    this.monitor = monitor;
    this.service = service;
    this.transformerRegistry = transformerRegistry;
    this.validatorRegistry = validatorRegistry;
    this.bucketName = bucketName;
    this.region = region;
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.endpointOverride = endpointOverride;
  }

  @POST
  public JsonObject initiateTransferProcess(JsonObject request) {
    validatorRegistry.validate(TRANSFER_REQUEST_TYPE, request).orElseThrow(ValidationFailureException::new);

    var transferRequest = transformerRegistry.transform(request, TransferRequest.class)
        .orElseThrow(InvalidRequestException::new);

    DataAddress dataDestination = getDataDestinationProperties();

    var tRequest = TransferRequest.Builder.newInstance().id(transferRequest.getId())
        .transferType(transferRequest.getTransferType()).callbackAddresses(transferRequest.getCallbackAddresses())
        .contractId(transferRequest.getContractId()).counterPartyAddress(transferRequest.getCounterPartyAddress())
        .protocol(transferRequest.getProtocol()).privateProperties(transferRequest.getPrivateProperties())
        .dataDestination(dataDestination).build();

    var createdTransfer = service.initiateTransfer(tRequest)
        .onSuccess(d -> monitor.debug(format("Transfer Process created %s", d.getId())))
        .orElseThrow(it -> mapToException(it, TransferProcess.class));

    var responseDto = IdResponse.Builder.newInstance().id(createdTransfer.getId())
        .createdAt(createdTransfer.getCreatedAt()).build();

    return transformerRegistry.transform(responseDto, JsonObject.class)
        .orElseThrow(f -> new EdcException("Error creating response body: " + f.getFailureDetail()));
  }

  private DataAddress getDataDestinationProperties() {
    Map<String, Object> properties = new HashMap<>();
    properties.put(CoreConstants.EDC_NAMESPACE + "bucketName", bucketName);
    properties.put(CoreConstants.EDC_NAMESPACE + "region", region);
    properties.put(CoreConstants.EDC_NAMESPACE + "type", "AmazonS3");
    properties.put(CoreConstants.EDC_NAMESPACE + "endpointOverride", endpointOverride);
    properties.put(CoreConstants.EDC_NAMESPACE + "accessKeyId", accessKey);
    properties.put(CoreConstants.EDC_NAMESPACE + "secretAccessKey", secretKey);
    return DataAddress.Builder.newInstance().properties(properties).build();
  }
}
