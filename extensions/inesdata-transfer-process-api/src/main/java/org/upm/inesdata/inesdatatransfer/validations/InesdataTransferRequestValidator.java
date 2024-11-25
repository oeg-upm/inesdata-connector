package org.upm.inesdata.inesdatatransfer.validations;

import jakarta.json.JsonObject;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.validator.jsonobject.JsonObjectValidator;
import org.eclipse.edc.validator.jsonobject.validators.LogDeprecatedValue;
import org.eclipse.edc.validator.jsonobject.validators.MandatoryValue;
import org.eclipse.edc.validator.jsonobject.validators.OptionalIdNotBlank;
import org.eclipse.edc.validator.spi.Validator;

import static org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferRequest.TRANSFER_REQUEST_ASSET_ID;
import static org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferRequest.TRANSFER_REQUEST_CONTRACT_ID;
import static org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferRequest.TRANSFER_REQUEST_COUNTER_PARTY_ADDRESS;
import static org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferRequest.TRANSFER_REQUEST_PROTOCOL;
import static org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferRequest.TRANSFER_REQUEST_TRANSFER_TYPE;

public class InesdataTransferRequestValidator {

    public static Validator<JsonObject> instance(Monitor monitor) {
        return JsonObjectValidator.newValidator()
                .verifyId(OptionalIdNotBlank::new)
                .verify(TRANSFER_REQUEST_ASSET_ID, path -> new LogDeprecatedValue(path, TRANSFER_REQUEST_ASSET_ID, "no attribute, as %s already provide such information".formatted(TRANSFER_REQUEST_CONTRACT_ID), monitor))
                .verify(TRANSFER_REQUEST_COUNTER_PARTY_ADDRESS, MandatoryValue::new)
                .verify(TRANSFER_REQUEST_CONTRACT_ID, MandatoryValue::new)
                .verify(TRANSFER_REQUEST_PROTOCOL, MandatoryValue::new)
                .verify(TRANSFER_REQUEST_TRANSFER_TYPE, MandatoryValue::new)
                .build();
    }

}