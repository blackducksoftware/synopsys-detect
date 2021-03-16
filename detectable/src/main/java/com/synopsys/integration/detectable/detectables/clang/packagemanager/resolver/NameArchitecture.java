/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver;

import java.util.Optional;

import javax.annotation.Nullable;

public class NameArchitecture {
    private final String name;
    @Nullable
    private final String architecture;

    public NameArchitecture(String name, @Nullable String architecture) {
        this.name = name;
        this.architecture = architecture;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getArchitecture() {
        return Optional.ofNullable(architecture);
    }
}
