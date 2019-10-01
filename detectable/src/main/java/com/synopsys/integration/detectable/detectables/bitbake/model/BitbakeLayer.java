package com.synopsys.integration.detectable.detectables.bitbake.model;

public class BitbakeLayer {
    private final String name;
    private final String version;

    public BitbakeLayer(final String name, final String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
