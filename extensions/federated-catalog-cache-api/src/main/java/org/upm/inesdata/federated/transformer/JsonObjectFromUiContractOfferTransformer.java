package org.upm.inesdata.federated.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import org.eclipse.edc.jsonld.spi.transformer.AbstractJsonLdTransformer;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.upm.inesdata.complexpolicy.model.UiPolicy;
import org.upm.inesdata.complexpolicy.model.UiPolicyConstraint;
import org.upm.inesdata.complexpolicy.model.UiPolicyExpression;
import org.upm.inesdata.complexpolicy.model.UiPolicyLiteral;
import org.upm.inesdata.federated.model.UiContractOffer;

import java.io.StringReader;

public class JsonObjectFromUiContractOfferTransformer extends AbstractJsonLdTransformer<UiContractOffer, JsonObject> {

    private final ObjectMapper objectMapper;
    private final JsonBuilderFactory jsonFactory;

    public JsonObjectFromUiContractOfferTransformer(ObjectMapper objectMapper, JsonBuilderFactory jsonFactory) {
        super(UiContractOffer.class, JsonObject.class);
        this.objectMapper = objectMapper;
        this.jsonFactory = jsonFactory;
    }

    @Override
    public Class<UiContractOffer> getInputType() {
        return UiContractOffer.class;
    }

    @Override
    public Class<JsonObject> getOutputType() {
        return JsonObject.class;
    }

    @Override
    public @Nullable JsonObject transform(@NotNull UiContractOffer input, @NotNull TransformerContext context) {
        JsonObjectBuilder jsonObjectBuilder = jsonFactory.createObjectBuilder();
        jsonObjectBuilder.add("contractOfferId", input.getContractOfferId());

        UiPolicy policy = input.getPolicy();
        if (policy != null) {
            JsonObjectBuilder policyBuilder = jsonFactory.createObjectBuilder();
            policyBuilder.add("policyJsonLd", policy.getPolicyJsonLd());

            if (policy.getExpression() != null) {
                policyBuilder.add("expression", transformPolicyExpression(policy.getExpression()));
            }

            JsonArrayBuilder errorsBuilder = jsonFactory.createArrayBuilder();
            if (policy.getErrors() != null) {
                for (String error : policy.getErrors()) {
                    errorsBuilder.add(error);
                }
            }
            policyBuilder.add("errors", errorsBuilder);

            jsonObjectBuilder.add("policy", policyBuilder);
        }

        return jsonObjectBuilder.build();
    }

    private JsonValue transformPolicyExpression(UiPolicyExpression expression) {
        if (expression == null) {
            return jsonFactory.createObjectBuilder().build();
        }

        JsonObjectBuilder expressionBuilder = jsonFactory.createObjectBuilder();
        expressionBuilder.add("type", expression.getType().name());

        if (expression.getExpressions() != null) {
            JsonArrayBuilder expressionsArrayBuilder = jsonFactory.createArrayBuilder();
            for (UiPolicyExpression subExpression : expression.getExpressions()) {
                expressionsArrayBuilder.add(transformPolicyExpression(subExpression));
            }
            expressionBuilder.add("expressions", expressionsArrayBuilder);
        }

        if (expression.getConstraint() != null) {
            expressionBuilder.add("constraint", transformPolicyConstraint(expression.getConstraint()));
        }

        return expressionBuilder.build();
    }

    private JsonValue transformPolicyConstraint(UiPolicyConstraint constraint) {
        if (constraint == null) {
            return jsonFactory.createObjectBuilder().build();
        }

        JsonObjectBuilder constraintBuilder = jsonFactory.createObjectBuilder();
        constraintBuilder.add("left", constraint.getLeft());
        constraintBuilder.add("operator", constraint.getOperator().name());
        constraintBuilder.add("right", transformPolicyLiteral(constraint.getRight()));

        return constraintBuilder.build();
    }

    private JsonValue transformPolicyLiteral(UiPolicyLiteral literal) {
        if (literal == null) {
            return jsonFactory.createObjectBuilder().build();
        }

        JsonObjectBuilder literalBuilder = jsonFactory.createObjectBuilder();
        literalBuilder.add("type", literal.getType().name());

        switch (literal.getType()) {
            case STRING -> literalBuilder.add("value", literal.getValue());
            case STRING_LIST -> {
                JsonArrayBuilder listBuilder = jsonFactory.createArrayBuilder();
                for (String value : literal.getValueList()) {
                    listBuilder.add(value);
                }
                literalBuilder.add("valueList", listBuilder);
            }
            case JSON -> {
                try {
                    JsonValue jsonValue = Json.createReader(new StringReader(literal.getValue())).readValue();
                    literalBuilder.add("value", jsonValue);
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing JSON value", e);
                }
            }
            default -> throw new IllegalArgumentException("Unsupported literal type: " + literal.getType());
        }

        return literalBuilder.build();
    }
}
