package com.synopsys.integration.detectable.detectables.yarn.packagejson;

import java.io.File;
import java.util.Objects;

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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkspacePackageJson that = (WorkspacePackageJson) o;
        return ((file != null && file.equals(that.file)) || file == null) 
                && packageJson.equals(that.packageJson)
                && dirRelativePath.equalsIgnoreCase(that.dirRelativePath);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.file);
        hash = 67 * hash + Objects.hashCode(this.packageJson);
        hash = 67 * hash + Objects.hashCode(this.dirRelativePath);
        return hash;
    }
}
