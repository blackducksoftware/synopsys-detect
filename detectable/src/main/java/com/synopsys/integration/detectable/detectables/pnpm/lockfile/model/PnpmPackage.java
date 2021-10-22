/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

public class PnpmPackage {
    public Boolean dev;
    public Boolean optional;
    public Map<String, String> dependencies;

    public boolean isDev() {
        return dev != null ? dev : false;
    }

    public boolean isOptional() {
        return optional != null ? optional : false;
    }

    public boolean hasDependencies() {
        return dependencies != null && !CollectionUtils.isEmpty(dependencies.entrySet());
    }
}
