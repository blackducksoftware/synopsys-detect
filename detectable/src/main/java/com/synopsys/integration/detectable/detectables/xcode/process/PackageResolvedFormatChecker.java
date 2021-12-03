package com.synopsys.integration.detectable.detectables.xcode.process;

import java.util.Arrays;
import java.util.function.BiConsumer;

import com.synopsys.integration.detectable.detectables.xcode.model.PackageResolved;

public class PackageResolvedFormatChecker {
    protected static final String[] KNOWN_FILE_FORMAT_VERSIONS = { "1" };

    public void handleVersionCompatibility(PackageResolved packageResolved, BiConsumer<String, String[]> unknownVersionHandler) {
        String fileFormatVersion = packageResolved.getFileFormatVersion();
        if (isVersionUnknown(fileFormatVersion)) {
            unknownVersionHandler.accept(fileFormatVersion, KNOWN_FILE_FORMAT_VERSIONS);
        }
    }

    private boolean isVersionUnknown(String fileFormatVersion) {
        return !Arrays.asList(KNOWN_FILE_FORMAT_VERSIONS).contains(fileFormatVersion);
    }
}
