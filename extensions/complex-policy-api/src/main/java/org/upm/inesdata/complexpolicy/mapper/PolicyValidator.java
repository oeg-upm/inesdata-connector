package org.upm.inesdata.complexpolicy.mapper;

import lombok.RequiredArgsConstructor;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.util.collection.CollectionUtil;
import org.eclipse.edc.util.string.StringUtils;


@RequiredArgsConstructor
public class PolicyValidator {

    public static final String ALLOWED_ACTION = "USE";

    public void validateOtherPolicyFieldsUnset(Policy policy, MappingErrors errors) {
        if (policy == null) {
            errors.add("Policy is null");
            return;
        }

        if (CollectionUtil.isEmpty(policy.getPermissions())) {
            errors.add("Policy has no permissions.");
        }

        if (CollectionUtil.isNotEmpty(policy.getProhibitions())) {
            errors.add("Policy has prohibitions, which are currently unsupported.");
        }

        if (CollectionUtil.isNotEmpty(policy.getObligations())) {
            errors.add("Policy has obligations, which are currently unsupported.");
        }

        if (!StringUtils.isNullOrBlank(policy.getInheritsFrom())) {
            errors.add("Policy has inheritsFrom, which is currently unsupported.");
        }

        if (!StringUtils.isNullOrBlank(policy.getAssigner())) {
            errors.add("Policy has an assigner, which is currently unsupported.");
        }

        if (!StringUtils.isNullOrBlank(policy.getAssignee())) {
            errors.add("Policy has an assignee, which is currently unsupported.");
        }

        if (policy.getExtensibleProperties() != null && !policy.getExtensibleProperties().isEmpty()) {
            errors.add("Policy has extensible properties.");
        }

        if (policy.getType() != PolicyType.SET) {
            errors.add("Policy does not have type SET, but %s, which is currently unsupported.".formatted(policy.getType()));
        }
    }

    public void validateOtherPermissionFieldsUnset(Permission permission, MappingErrors errors) {
        if (permission == null) {
            errors.add("Permission is null.");
            return;
        }

        if (CollectionUtil.isNotEmpty(permission.getDuties())) {
            errors.add("Permission has duties, which is currently unsupported.");
        }

        validateAction(permission.getAction(), errors.forChildObject("action"));
    }

    private void validateAction(Action action, MappingErrors errors) {
        if (action == null) {
            errors.add("Action is null.");
            return;
        }

        if (!ALLOWED_ACTION.equals(action.getType())) {
            errors.add("Action has a type that is not '%s', but '%s'.".formatted(ALLOWED_ACTION, action.getType()));
        }

        if (!StringUtils.isNullOrBlank(action.getIncludedIn())) {
            errors.add("Action has a value for includedIn, which is currently unsupported.");
        }

        if (action.getConstraint() != null) {
            errors.add("Action has a constraint, which is currently unsupported.");
        }
    }
}
