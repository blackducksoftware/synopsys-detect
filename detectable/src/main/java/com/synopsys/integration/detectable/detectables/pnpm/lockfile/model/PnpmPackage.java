package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

public class PnpmPackage {
    public String dev;
    public Map<String, String> dependencies;

    public boolean isDev() {
        if (dev == null) {
            return false;
        }
        return dev.equals("true");
    }

    public boolean hasDependencies() {
        return dependencies != null && !CollectionUtils.isEmpty(dependencies.entrySet());
    }
}
