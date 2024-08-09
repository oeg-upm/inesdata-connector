package org.upm.inesdata.complexpolicy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Type-Safe OpenAPI generator friendly ODLR policy subset.")
public class UiPolicy {
    @Schema(description = "EDC Policy JSON-LD. This is required because the EDC requires the " +
        "full policy when initiating contract negotiations.", requiredMode = RequiredMode.REQUIRED)
    private String policyJsonLd;

    @Schema(description = "Policy expression")
    private UiPolicyExpression expression;

    @Schema(description = "When trying to reduce the policy JSON-LD to our opinionated subset of functionalities, " +
        "many fields and functionalities are unsupported. Should any discrepancies occur during " +
        "the mapping process, we'll collect them here.", requiredMode = RequiredMode.REQUIRED)
    private List<String> errors;
}
