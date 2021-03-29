/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang.compilecommand;

import com.synopsys.integration.exception.IntegrationException;

public class OverrideOptionWithNoValueException extends IntegrationException {
    public OverrideOptionWithNoValueException(String message) {
        super(message);
    }
}
