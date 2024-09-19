package com.blackduck.integration.detectable.detectables.clang.compilecommand;

import com.blackduck.integration.exception.IntegrationException;

public class OverrideOptionWithNoValueException extends IntegrationException {
    public OverrideOptionWithNoValueException(String message) {
        super(message);
    }
}
