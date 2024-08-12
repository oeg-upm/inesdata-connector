package org.upm.inesdata.complexpolicy.exception;

import org.eclipse.edc.spi.result.Failure;

public class FailedMappingException extends RuntimeException {
    public FailedMappingException(String message) {
        super(message);
    }

    public static FailedMappingException ofFailure(Failure failure) {
        return new FailedMappingException(failure.getFailureDetail());
    }
}
