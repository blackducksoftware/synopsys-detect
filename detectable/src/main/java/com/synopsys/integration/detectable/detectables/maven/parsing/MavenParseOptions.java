/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.maven.parsing;

public class MavenParseOptions {
    private final boolean includePlugins;

    public MavenParseOptions(final boolean includePlugins) {
        this.includePlugins = includePlugins;
    }

    public boolean isIncludePlugins() {
        return includePlugins;
    }
}
