package com.synopsys.integration.detectable.detectables.clang.packagemanager;

import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ClangPackageManagerResolver;

public class ClangPackageManager {
    private final ClangPackageManagerInfo packageManagerInfo;
    private final ClangPackageManagerResolver packageResolver;

    public ClangPackageManager(ClangPackageManagerInfo packageManagerInfo, ClangPackageManagerResolver packageResolver) {
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
