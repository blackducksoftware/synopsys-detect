package com.synopsys.integration.detectable.detectables.swift.lock.data;

import com.synopsys.integration.util.Stringable;

public class PackageResolvedFormat extends Stringable {
    public static final PackageResolvedFormat V_1 = new PackageResolvedFormat("1");
    public static final PackageResolvedFormat V_2 = new PackageResolvedFormat("2");

    public static PackageResolvedFormat UNKNOWN(String foundVersion) {
        return new PackageResolvedFormat(foundVersion);
    }

    private final String versionString;

    private PackageResolvedFormat(String versionString) {
        this.versionString = versionString;
    }

    public String getVersionString() {
        return versionString;
    }
}
