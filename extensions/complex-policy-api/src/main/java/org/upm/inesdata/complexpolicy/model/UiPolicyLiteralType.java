package org.upm.inesdata.complexpolicy.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Supported Types of values for the right hand side of an expression", enumAsRef = true)
public enum UiPolicyLiteralType {
    STRING,
    STRING_LIST,
    JSON
}
