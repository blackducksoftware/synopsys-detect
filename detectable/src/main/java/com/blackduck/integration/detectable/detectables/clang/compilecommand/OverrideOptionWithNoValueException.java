package com.blackduck.integration.detectable.detectables.clang.compilecommand;

import com.synopsys.integration.exception.IntegrationException;

public class OverrideOptionWithNoValueException extends IntegrationException {
    public OverrideOptionWithNoValueException(String message) {
        super(message);
    }
}
