/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver;

import com.synopsys.integration.exception.IntegrationException;

public class NotOwnedByAnyPkgException extends IntegrationException {

    public NotOwnedByAnyPkgException(final String message) {
        super(message);
    }
}
