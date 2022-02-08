package com.synopsys.integration.detectable.detectables.yarn.packagejson;

import java.io.File;

public class WorkspacePackageJson {
    private final File file;
    private final NullSafePackageJson packageJson;
    private final String dirRelativePath;

    public WorkspacePackageJson(File packageJsonFile, NullSafePackageJson packageJson, String dirRelativePath) {
        this.file = packageJsonFile;
        this.packageJson = packageJson;
        this.dirRelativePath = dirRelativePath;
    }

    public File getFile() {
        return file;
    }

    public File getDir() {
        return file.getParentFile();
    }

    public NullSafePackageJson getPackageJson() {
        return packageJson;
    }

    public String getDirRelativePath() {
        return dirRelativePath;
    }
}
