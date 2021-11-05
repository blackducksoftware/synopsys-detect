/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class PnpmLockYaml {
    @Nullable
    public Map<String, PnpmProjectPackage> importers;

    @Nullable
    public Map<String, String> dependencies;

    @Nullable
    public Map<String, String> devDependencies;

    @Nullable
    public Map<String, String> optionalDependencies;

    @Nullable
    public Map<String, PnpmPackage> packages;

}
