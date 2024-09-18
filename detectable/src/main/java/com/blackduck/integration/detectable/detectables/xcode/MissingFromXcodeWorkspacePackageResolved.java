package com.blackduck.integration.detectable.detectables.xcode;

import java.io.File;

import com.blackduck.integration.detectable.detectables.swift.lock.SwiftPackageResolvedDetectable;
import com.blackduck.integration.detectable.detectable.result.FailedDetectableResult;

public class MissingFromXcodeWorkspacePackageResolved extends FailedDetectableResult {
    private final File searchDirectory;
    private final File workspaceDirectory;

    public MissingFromXcodeWorkspacePackageResolved(File searchDirectory, File workspaceDirectory) {
        this.searchDirectory = searchDirectory;
        this.workspaceDirectory = workspaceDirectory;
    }

    @Override
    public String toDescription() {
        return String.format(
            "Failed to find %s file within the Xcode project (%s) as defined in the Xcode workspace (%s)",
            SwiftPackageResolvedDetectable.PACKAGE_RESOLVED_FILENAME,
            searchDirectory.getPath(),
            workspaceDirectory.getAbsolutePath()
        );
    }
}
