/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm.lockfile.model;

public class NpmRequires {
    private final String name;
    private final String fuzzyVersion;

    public NpmRequires(final String name, final String fuzzyVersion) {
        this.name = name;
        this.fuzzyVersion = fuzzyVersion;
    }

    public String getName() {
        return name;
    }

    public String getFuzzyVersion() {
        return fuzzyVersion;
    }
}