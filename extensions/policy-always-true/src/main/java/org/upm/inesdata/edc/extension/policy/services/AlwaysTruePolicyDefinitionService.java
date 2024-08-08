/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package org.upm.inesdata.edc.extension.policy.services;

import org.eclipse.edc.connector.controlplane.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.controlplane.services.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.upm.inesdata.edc.extension.policy.AlwaysTruePolicyConstants;

/**
 * Creates policy definition &quot;always-true&quot;.
 */
public class AlwaysTruePolicyDefinitionService {
    private final PolicyDefinitionService policyDefinitionService;

    public AlwaysTruePolicyDefinitionService(PolicyDefinitionService policyDefinitionService) {
        this.policyDefinitionService = policyDefinitionService;
    }

    /**
     * Checks if policy definition &quot;always-true&quot; exists
     *
     * @return if exists
     */
    public boolean exists() {
        return policyDefinitionService.findById(AlwaysTruePolicyConstants.POLICY_DEFINITION_ID) != null;
    }

    /**
     * Creates policy definition &quot;always-true&quot;.
     */
    public void create() {
        var alwaysTrueConstraint = AtomicConstraint.Builder.newInstance()
                .leftExpression(new LiteralExpression(AlwaysTruePolicyConstants.EXPRESSION_LEFT_VALUE))
                .operator(Operator.EQ)
                .rightExpression(new LiteralExpression(AlwaysTruePolicyConstants.EXPRESSION_RIGHT_VALUE))
                .build();
        var alwaysTruePermission = Permission.Builder.newInstance()
                .action(Action.Builder.newInstance().type("USE").build())
                .constraint(alwaysTrueConstraint)
                .build();
        var policy = Policy.Builder.newInstance()
                .permission(alwaysTruePermission)
                .build();
        var policyDefinition = PolicyDefinition.Builder.newInstance()
                .id(AlwaysTruePolicyConstants.POLICY_DEFINITION_ID)
                .policy(policy)
                .build();
        policyDefinitionService.create(policyDefinition);
    }
}
