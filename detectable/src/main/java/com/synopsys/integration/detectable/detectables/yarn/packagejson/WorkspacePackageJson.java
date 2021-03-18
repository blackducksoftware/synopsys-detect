package com.synopsys.integration.detectable.detectables.yarn.packagejson;

import java.io.File;

import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.util.Stringable;

public class WorkspacePackageJson extends Stringable {
    private final File packageJsonFile;
    private final PackageJson packageJson;

    public WorkspacePackageJson(File packageJsonFile, PackageJson packageJson) {
        this.packageJsonFile = packageJsonFile;
        this.packageJson = packageJson;
    }

    public File getPackageJsonFile() {
        return packageJsonFile;
    }

    public PackageJson getPackageJson() {
        return packageJson;
    }
}
