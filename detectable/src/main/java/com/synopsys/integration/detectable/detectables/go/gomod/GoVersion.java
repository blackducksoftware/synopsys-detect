package com.synopsys.integration.detectable.detectables.go.gomod;

public class GoVersion {
    private final int majorVersion;
    private final int minorVersion;

    public GoVersion(int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }
}
