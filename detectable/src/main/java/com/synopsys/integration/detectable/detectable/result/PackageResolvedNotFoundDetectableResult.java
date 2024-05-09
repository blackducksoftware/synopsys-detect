package com.synopsys.integration.detectable.detectable.result;

import com.synopsys.integration.detectable.detectables.swift.cli.SwiftCliDetectable;
import com.synopsys.integration.detectable.detectables.swift.lock.SwiftPackageResolvedDetectable;

public class PackageResolvedNotFoundDetectableResult extends FailedDetectableResult {
    private static final String FORMAT = "A %s was located in %s, but the %s file was NOT located. Please build the Swift project in that location and try again.";

    public PackageResolvedNotFoundDetectableResult(String directoryPath) {
        super(String.format(
            FORMAT,
            SwiftCliDetectable.PACKAGE_SWIFT_FILENAME,
            directoryPath,
            SwiftPackageResolvedDetectable.PACKAGE_RESOLVED_FILENAME
        ));
    }
}