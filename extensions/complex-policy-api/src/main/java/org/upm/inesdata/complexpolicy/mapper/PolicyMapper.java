package org.upm.inesdata.complexpolicy.mapper;


import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.controlplane.policy.spi.PolicyDefinition;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.upm.inesdata.complexpolicy.exception.FailedMappingException;
import org.upm.inesdata.complexpolicy.model.PolicyDefinitionDto;
import org.upm.inesdata.complexpolicy.model.UiPolicy;
import org.upm.inesdata.complexpolicy.model.UiPolicyExpression;
import org.upm.inesdata.complexpolicy.utils.JsonUtils;


@RequiredArgsConstructor
public class PolicyMapper {
    private final ExpressionExtractor expressionExtractor;
    private final ExpressionMapper expressionMapper;
    private final TypeTransformerRegistry typeTransformerRegistry;

    /**
     * Builds a simplified UI Policy Model from an ODRL Policy.
     * <p>
     * This operation is lossy.
     *
     * @param policy ODRL policy
     * @return ui policy
     */
    public UiPolicy buildUiPolicy(Policy policy) {
        MappingErrors errors = MappingErrors.root();

        var expression = expressionExtractor.getPermissionExpression(policy, errors);

        return UiPolicy.builder()
            .policyJsonLd(JsonUtils.toJson(buildPolicyJsonLd(policy)))
            .expression(expression)
            .errors(errors.getErrors())
            .build();
    }

    /**
     * Builds an ODRL Policy from our simplified UI Policy Model.
     * <p>
     * This operation is lossless.
     *
     * @param expression policy
     * @return ODRL policy
     */
    public Policy buildPolicy(UiPolicyExpression expression) {
        var constraints = expressionMapper.buildConstraint(expression);

        var action = Action.Builder.newInstance().type(PolicyValidator.ALLOWED_ACTION).build();

        var permission = Permission.Builder.newInstance()
            .action(action)
            .constraints(constraints.stream().toList())
            .build();

        return Policy.Builder.newInstance()
            .type(PolicyType.SET)
            .permission(permission)
            .build();
    }

    /**
     * Maps an ODRL Policy from JSON-LD to the Core EDC Type.
     * <p>
     * This operation is lossless.
     *
     * @param policyJsonLd policy JSON-LD
     * @return {@link Policy}
     */
    public Policy buildPolicy(JsonObject policyJsonLd) {
        return typeTransformerRegistry.transform(policyJsonLd, Policy.class)
            .orElseThrow(FailedMappingException::ofFailure);
    }

    /**
     * Maps an ODRL Policy from JSON-LD to the Core EDC Type.
     * <p>
     * This operation is lossless.
     *
     * @param policyJsonLd policy JSON-LD
     * @return {@link Policy}
     */
    public Policy buildPolicy(String policyJsonLd) {
        return buildPolicy(JsonUtils.parseJsonObj(policyJsonLd));
    }

    /**
     * Maps an ODRL Policy from the Core EDC Type to the JSON-LD.
     * <p>
     * This operation is lossless.
     *
     * @param policy {@link Policy}
     * @return policy JSON-LD
     */
    public JsonObject buildPolicyJsonLd(Policy policy) {
        return typeTransformerRegistry.transform(policy, JsonObject.class)
            .orElseThrow(FailedMappingException::ofFailure);
    }

    /**
     * Builds a simplified policy definition DTO from a policy definition
     * <p>
     * This operation is lossy.
     *
     * @param policyDefinition policy definition
     * @return ui policy
     */
    public PolicyDefinitionDto buildPolicyDefinitionDto(PolicyDefinition policyDefinition) {
        var policy = buildUiPolicy(policyDefinition.getPolicy());
        return PolicyDefinitionDto.builder()
                .policyDefinitionId(policyDefinition.getId())
                .policy(policy)
                .build();
    }
}
