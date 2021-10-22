/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

public class PnpmLockYaml {
    public Map<String, String> dependencies;

    public Map<String, String> devDependencies;

    public Map<String, String> optionalDependencies;

    public Map<String, PnpmPackage> packages;

}
