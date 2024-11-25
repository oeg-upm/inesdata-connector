package org.upm.inesdata.federated.model;

import jakarta.json.JsonObject;
import lombok.Data;

@Data
public class DspContractOffer {
    private final String contractOfferId;
    private final JsonObject policyJsonLd;
}