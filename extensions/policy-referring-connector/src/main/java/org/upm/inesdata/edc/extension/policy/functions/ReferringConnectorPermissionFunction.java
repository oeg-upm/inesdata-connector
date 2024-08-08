package org.upm.inesdata.edc.extension.policy.functions;

import org.eclipse.edc.policy.engine.spi.AtomicConstraintFunction;
import org.eclipse.edc.policy.engine.spi.PolicyContext;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.spi.monitor.Monitor;

/** AtomicConstraintFunction to validate the referring connector claim for edc permissions. */
public class ReferringConnectorPermissionFunction extends AbstractReferringConnectorValidation
        implements AtomicConstraintFunction<Permission> {

    public ReferringConnectorPermissionFunction(Monitor monitor) {
        super(monitor);
    }

    @Override
    public boolean evaluate(Operator operator, Object rightValue, Permission rule, PolicyContext context) {
        return evaluate(operator, rightValue, context);
    }
}