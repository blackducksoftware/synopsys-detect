package com.synopsys.integration.detect.lifecycle.boot.product.version;

public class BlackDuckVersion {
    private final int major;
    private final int minor;
    private final int patch;

    public BlackDuckVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public boolean isAtLeast(BlackDuckVersion other) {
        if (major > other.getMajor()) {
            return true;
        }
        if (major < other.getMajor()) {
            return false;
        }
        if (minor > other.getMinor()) {
            return true;
        }
        if (minor < other.getMinor()) {
            return false;
        }
        if (patch > other.getPatch()) {
            return true;
        }
        if (patch < other.getPatch()) {
            return false;
        }
        return true;
    }
}
