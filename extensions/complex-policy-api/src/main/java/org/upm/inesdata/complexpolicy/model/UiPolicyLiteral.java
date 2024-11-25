package org.upm.inesdata.complexpolicy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Sum type: A String, a list of Strings or a generic JSON value.")
public class UiPolicyLiteral {
    @Schema(description = "Value Type", requiredMode = RequiredMode.REQUIRED)
    private UiPolicyLiteralType type;

    @Schema(description = "Only for types STRING and JSON")
    private String value;

    @Schema(description = "Only for type STRING_LIST")
    private List<String> valueList;

    public static UiPolicyLiteral ofString(String string) {
        return UiPolicyLiteral.builder()
            .type(UiPolicyLiteralType.STRING)
            .value(string)
            .build();
    }

    public static UiPolicyLiteral ofJson(String jsonString) {
        return UiPolicyLiteral.builder()
            .type(UiPolicyLiteralType.JSON)
            .value(jsonString)
            .build();
    }

    public static UiPolicyLiteral ofStringList(Collection<String> strings) {
        return UiPolicyLiteral.builder()
            .type(UiPolicyLiteralType.STRING_LIST)
            .valueList(new ArrayList<>(strings))
            .build();
    }
}
