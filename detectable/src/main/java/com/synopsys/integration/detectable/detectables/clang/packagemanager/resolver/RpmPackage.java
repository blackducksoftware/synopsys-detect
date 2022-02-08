package com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver;

public class RpmPackage {
    private final String epoch;
    private final String name;
    private final String version;
    private final String arch;

    public RpmPackage(String epoch, String name, String version, String arch) {
        super();
        this.epoch = epoch;
        this.name = name;
        this.version = version;
        this.arch = arch;
    }

    public String getEpoch() {
        return epoch;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getArch() {
        return arch;
    }
}
