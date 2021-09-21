/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.maven.parsing;

public class MavenParseOptions {
    private final boolean includePlugins;
    private final boolean enableLegacyMode;

    public MavenParseOptions(final boolean includePlugins, final boolean enableLegacyMode) {
        this.includePlugins = includePlugins;
        this.enableLegacyMode = enableLegacyMode;
    }

    public boolean isIncludePlugins() {
        return includePlugins;
    }

    public boolean isEnableLegacyMode() {
        return enableLegacyMode;
    }
}
