package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

public class PnpmLockYaml {
    public Map<String, String> dependencies;

    public Map<String, String> devDependencies;

    public Map<String, PnpmPackage> packages;

}
