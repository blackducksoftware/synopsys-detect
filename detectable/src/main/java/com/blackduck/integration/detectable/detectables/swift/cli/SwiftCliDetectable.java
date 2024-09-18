package com.blackduck.integration.detectable.detectables.swift.cli;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.DetectableAccuracyType;
import com.blackduck.integration.detectable.detectable.Requirements;
import com.blackduck.integration.detectable.detectable.annotation.DetectableInfo;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.detectable.executable.ExecutableFailedException;
import com.blackduck.integration.detectable.detectable.executable.resolver.SwiftResolver;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Swift CLI", language = "Swift", forge = "Swift.org", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: Package.swift. Executables: swift.")
public class SwiftCliDetectable extends Detectable {
    public static final String PACKAGE_SWIFT_FILENAME = "Package.swift";

    private final FileFinder fileFinder;
    private final SwiftExtractor swiftExtractor;
    private final SwiftResolver swiftResolver;

    private ExecutableTarget swiftExecutable;

    public SwiftCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, SwiftExtractor swiftExtractor, SwiftResolver swiftResolver) {
        super(environment);
        this.fileFinder = fileFinder;
        this.swiftExtractor = swiftExtractor;
        this.swiftResolver = swiftResolver;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(PACKAGE_SWIFT_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        swiftExecutable = requirements.executable(swiftResolver::resolveSwift, "swift");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException {
        return swiftExtractor.extract(environment.getDirectory(), swiftExecutable);
    }

}
