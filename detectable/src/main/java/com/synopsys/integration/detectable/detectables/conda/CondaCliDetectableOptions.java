/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.conda;

import java.util.Optional;

public class CondaCliDetectableOptions {
    private final String condaEnvironmentName;

    public CondaCliDetectableOptions(final String condaEnvironmentName) {
        this.condaEnvironmentName = condaEnvironmentName;
    }

    public Optional<String> getCondaEnvironmentName() {
        return Optional.ofNullable(condaEnvironmentName);
    }
}
