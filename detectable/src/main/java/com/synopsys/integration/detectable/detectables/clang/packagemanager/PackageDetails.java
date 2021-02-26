/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang.packagemanager;

import com.synopsys.integration.util.Stringable;

public class PackageDetails extends Stringable {
    private final String packageName;
    private final String packageVersion;
    private final String packageArch;

    public PackageDetails(String packageName, String packageVersion, String packageArch) {
        this.packageName = packageName;
        this.packageVersion = packageVersion;
        this.packageArch = packageArch;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public String getPackageArch() {
        return packageArch;
    }
}
