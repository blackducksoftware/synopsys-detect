/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.projectinspector;

import org.jetbrains.annotations.Nullable;

public class ProjectInspectorOptions {
    private final String additionalArguments;

    public ProjectInspectorOptions(@Nullable String additionalArguments) {
        this.additionalArguments = additionalArguments;
    }

    @Nullable
    public String getAdditionalArguments() {
        return additionalArguments;
    }
}
