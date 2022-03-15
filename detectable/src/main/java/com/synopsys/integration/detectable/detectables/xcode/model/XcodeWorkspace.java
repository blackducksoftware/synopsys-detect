package com.synopsys.integration.detectable.detectables.xcode.model;

import java.util.List;

public class XcodeWorkspace {
    private final String formatVersion;
    private final List<XcodeFileReference> fileReferences;

    public XcodeWorkspace(String formatVersion, List<XcodeFileReference> fileReferences) {
        this.formatVersion = formatVersion;
        this.fileReferences = fileReferences;
    }

    public String getFormatVersion() {
        return formatVersion;
    }

    public List<XcodeFileReference> getFileReferences() {
        return fileReferences;
    }
}
