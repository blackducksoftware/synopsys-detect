package com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver;

import com.synopsys.integration.exception.IntegrationException;

public class NotOwnedByAnyPkgException extends IntegrationException {

    public NotOwnedByAnyPkgException(final String message) {
        super(message);
    }
}
