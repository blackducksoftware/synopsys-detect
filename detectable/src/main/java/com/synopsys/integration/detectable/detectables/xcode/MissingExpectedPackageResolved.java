package com.synopsys.integration.detectable.detectables.xcode;

import java.io.File;

import com.synopsys.integration.detectable.detectable.result.FailedDetectableResult;
import com.synopsys.integration.detectable.detectables.swift.lock.SwiftPackageResolvedDetectable;

public class MissingExpectedPackageResolved extends FailedDetectableResult {
    private final File searchDirectory;
    private final File workspaceDirectory;

    public MissingExpectedPackageResolved(File searchDirectory, File workspaceDirectory) {
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
