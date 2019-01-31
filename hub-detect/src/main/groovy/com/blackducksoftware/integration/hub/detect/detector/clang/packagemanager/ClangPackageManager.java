package com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager;

import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.resolver.ClangPackageManagerResolver;

public class ClangPackageManager {
    private final ClangPackageManagerInfo packageManagerInfo;
    private final ClangPackageManagerResolver packageResolver;

    public ClangPackageManager(final ClangPackageManagerInfo packageManagerInfo, final ClangPackageManagerResolver packageResolver) {
        this.packageManagerInfo = packageManagerInfo;
        this.packageResolver = packageResolver;
    }

    public ClangPackageManagerInfo getPackageManagerInfo() {
        return packageManagerInfo;
    }

    public ClangPackageManagerResolver getPackageResolver() {
        return packageResolver;
    }
}
