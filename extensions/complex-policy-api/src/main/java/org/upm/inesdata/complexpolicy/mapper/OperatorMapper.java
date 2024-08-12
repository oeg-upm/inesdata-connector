package org.upm.inesdata.complexpolicy.mapper;

import lombok.RequiredArgsConstructor;
import org.eclipse.edc.policy.model.Operator;
import org.upm.inesdata.complexpolicy.model.OperatorDto;

@RequiredArgsConstructor
public class OperatorMapper {
    public OperatorDto getOperatorDto(String operator) {
        return OperatorDto.valueOf(operator.toUpperCase());
    }

    public OperatorDto getOperatorDto(Operator operator) {
        return OperatorDto.valueOf(operator.name());
    }

    public Operator getOperator(OperatorDto operatorDto) {
        return Operator.valueOf(operatorDto.name());
    }
}
