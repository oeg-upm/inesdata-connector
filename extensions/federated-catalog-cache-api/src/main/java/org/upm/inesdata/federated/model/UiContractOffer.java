package org.upm.inesdata.federated.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.upm.inesdata.complexpolicy.model.UiPolicy;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Catalog Data Offer's Contract Offer as required by the UI")
public class UiContractOffer {
    @Schema(description = "Contract Offer ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractOfferId;

    @Schema(description = "Policy", requiredMode = Schema.RequiredMode.REQUIRED)
    private UiPolicy policy;

    public String getContractOfferId() {
        return contractOfferId;
    }

    public void setContractOfferId(String contractOfferId) {
        this.contractOfferId = contractOfferId;
    }

    public UiPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(UiPolicy policy) {
        this.policy = policy;
    }
}
