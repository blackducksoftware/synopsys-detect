package com.blackduck.integration.detectable.detectables.clang.packagemanager.resolver;

import com.blackduck.integration.exception.IntegrationException;

public class NotOwnedByAnyPkgException extends IntegrationException {

    public NotOwnedByAnyPkgException(String message) {
        super(message);
    }
}
