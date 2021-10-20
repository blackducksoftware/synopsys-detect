package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

public class PnpmPackageId {
    private final String name;
    private final String version;

    public PnpmPackageId(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getPackageIndentifier() {
        return String.format("/%s/%s", name, version);
    }
}
