/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm.packagejson;

public class NpmPackageJsonParseDetectableOptions {
    private final boolean includeDevDependencies;
    private final boolean includePeerDependencies;

    public NpmPackageJsonParseDetectableOptions(boolean includeDevDependencies, boolean includePeerDependencies) {
        this.includeDevDependencies = includeDevDependencies;
        this.includePeerDependencies = includePeerDependencies;
    }

    public boolean shouldIncludeDevDependencies() {
        return includeDevDependencies;
    }

    public boolean shouldIncludePeerDependencies() {
        return includePeerDependencies;
    }
}
