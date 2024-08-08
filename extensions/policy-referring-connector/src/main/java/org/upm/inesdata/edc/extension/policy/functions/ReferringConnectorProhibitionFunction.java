package org.upm.inesdata.edc.extension.policy.functions;

import org.eclipse.edc.policy.engine.spi.AtomicConstraintFunction;
import org.eclipse.edc.policy.engine.spi.PolicyContext;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Prohibition;
import org.eclipse.edc.spi.monitor.Monitor;

/** AtomicConstraintFunction to validate the referring connector claim  edc prohibitions. */
public class ReferringConnectorProhibitionFunction extends AbstractReferringConnectorValidation
        implements AtomicConstraintFunction<Prohibition> {

    public ReferringConnectorProhibitionFunction(Monitor monitor) {
        super(monitor);
    }

    @Override
    public boolean evaluate(Operator operator, Object rightValue, Prohibition rule, PolicyContext context) {
        return evaluate(operator, rightValue, context);
    }
}