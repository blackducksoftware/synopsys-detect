package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

public class PnpmPackage {
    public String dev; //TODO- do we need this value?
    public Map<String, String> dependencies;
}
