package com.synopsys.integration.detect.tool.signaturescanner;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.synopsys.integration.detect.workflow.file.DetectFileUtils;

public class SignatureScanPath {
    private File targetPath;
    private String targetCanonicalPath;
    private final Set<String> exclusions = new HashSet<>();

    public File getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(File targetPath) {
        this.targetPath = targetPath;
        this.targetCanonicalPath = DetectFileUtils.tryGetCanonicalPath(targetPath);
    }

    public Set<String> getExclusions() {
        return exclusions;
    }

    public String getTargetCanonicalPath() {
        return targetCanonicalPath;
    }
}
