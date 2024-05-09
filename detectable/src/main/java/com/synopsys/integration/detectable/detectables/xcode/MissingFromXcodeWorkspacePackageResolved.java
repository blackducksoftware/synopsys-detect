package com.synopsys.integration.detectable.detectables.xcode;

import java.io.File;

import com.synopsys.integration.detectable.detectable.result.FailedDetectableResult;
import com.synopsys.integration.detectable.detectables.swift.lock.SwiftPackageResolvedDetectable;

public class MissingFromXcodeWorkspacePackageResolved extends FailedDetectableResult {
    private static final String FORMAT = "Failed to find %s file within the Xcode project (%s) as defined in the Xcode workspace (%s)";

    public MissingFromXcodeWorkspacePackageResolved(File searchDirectory, File workspaceDirectory) {
        super(String.format(
            FORMAT,
            SwiftPackageResolvedDetectable.PACKAGE_RESOLVED_FILENAME,
            searchDirectory.getPath(),
            workspaceDirectory.getAbsolutePath()
        ));
    }
}
