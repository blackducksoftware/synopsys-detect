/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.parse;

public class YarnLockDependency {
    private final String name;
    private final String version;
    private final boolean optional;

    public YarnLockDependency(final String name, final String version, final boolean optional) {
        this.name = name;
        this.version = version;
        this.optional = optional;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public boolean isOptional() {
        return optional;
    }
}
