package com.synopsys.integration.detectable.detectables.xcode.parse;

import java.util.Arrays;
import java.util.function.BiConsumer;

import com.synopsys.integration.detectable.detectables.xcode.model.XcodeWorkspace;

public class XcodeWorkspaceFormatChecker {
    protected static final String[] KNOWN_FILE_FORMAT_VERSIONS = { "1.0" };

    public void checkForVersionCompatibility(XcodeWorkspace xcodeWorkspace, BiConsumer<String, String[]> unknownVersionHandler) {
        String fileFormatVersion = xcodeWorkspace.getFormatVersion();
        if (isVersionUnknown(fileFormatVersion)) {
            unknownVersionHandler.accept(fileFormatVersion, KNOWN_FILE_FORMAT_VERSIONS);
        }
    }

    private boolean isVersionUnknown(String fileFormatVersion) {
        return !Arrays.asList(KNOWN_FILE_FORMAT_VERSIONS).contains(fileFormatVersion);
    }
}
