package org.upm.inesdata.edc.extension.policy;

import org.upm.inesdata.edc.extension.policy.services.AlwaysTruePolicyDefinitionService;
import org.upm.inesdata.edc.extension.policy.services.AlwaysTruePolicyService;
import org.eclipse.edc.connector.controlplane.services.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import static org.upm.inesdata.edc.extension.policy.AlwaysTruePolicyConstants.EXTENSION_NAME;

/**
 * Extension: Policy Definition "Always True".
 */
public class AlwaysTruePolicyExtension implements ServiceExtension {
    @Inject
    private Monitor monitor;

    @Inject
    private RuleBindingRegistry ruleBindingRegistry;

    @Inject
    private PolicyDefinitionService policyDefinitionService;

    @Inject
    private PolicyEngine policyEngine;

    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var alwaysTruePolicyService = new AlwaysTruePolicyService(ruleBindingRegistry, policyEngine);
        alwaysTruePolicyService.registerPolicy();
    }

    @Override
    public void start() {
        var alwaysTruePolicyDefinitionService = new AlwaysTruePolicyDefinitionService(policyDefinitionService);
        if (!alwaysTruePolicyDefinitionService.exists()) {
            monitor.info("Creating Always True Policy Definition.");
            alwaysTruePolicyDefinitionService.create();
        } else {
            monitor.debug("Skipping Always True Policy Definition creation, policy definition already exists.");
        }
    }
}
