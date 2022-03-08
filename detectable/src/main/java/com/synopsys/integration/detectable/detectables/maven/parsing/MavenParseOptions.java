package com.synopsys.integration.detectable.detectables.maven.parsing;

public class MavenParseOptions {
    private final boolean includePlugins;
    private final boolean enableLegacyMode;

    public MavenParseOptions(boolean includePlugins, boolean enableLegacyMode) {
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
