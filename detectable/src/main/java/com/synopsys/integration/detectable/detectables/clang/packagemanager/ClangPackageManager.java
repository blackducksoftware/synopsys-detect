/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang.packagemanager;

import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ClangPackageManagerResolver;

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
