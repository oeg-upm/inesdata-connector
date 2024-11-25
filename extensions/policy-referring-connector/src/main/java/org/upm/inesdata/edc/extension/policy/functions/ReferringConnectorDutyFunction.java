package org.upm.inesdata.edc.extension.policy.functions;

import org.eclipse.edc.policy.engine.spi.AtomicConstraintFunction;
import org.eclipse.edc.policy.engine.spi.PolicyContext;
import org.eclipse.edc.policy.model.Duty;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.spi.monitor.Monitor;

/** AtomicConstraintFunction to validate the referring connector claim for edc duties. */
public class ReferringConnectorDutyFunction extends AbstractReferringConnectorValidation
        implements AtomicConstraintFunction<Duty> {

    public ReferringConnectorDutyFunction(Monitor monitor) {
        super(monitor);
    }

    @Override
    public boolean evaluate(Operator operator, Object rightValue, Duty rule, PolicyContext context) {
        return evaluate(operator, rightValue, context);
    }
}