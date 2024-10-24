package com.blackduck.integration.detectable.detectables.swift.lock;

import java.io.File;
import java.io.IOException;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.detectable.DetectableAccuracyType;
import com.blackduck.integration.detectable.detectable.Requirements;
import com.blackduck.integration.detectable.detectable.annotation.DetectableInfo;
import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.detectable.executable.ExecutableFailedException;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.detectable.result.PackageResolvedNotFoundDetectableResult;
import com.blackduck.integration.detectable.detectables.swift.lock.model.PackageResolvedResult;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Swift Lock", language = "Swift", forge = "Swift.org", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: Package.swift, Package.resolved")
public class SwiftPackageResolvedDetectable extends Detectable {
    public static final String PACKAGE_SWIFT_FILENAME = "Package.swift";
    public static final String PACKAGE_RESOLVED_FILENAME = "Package.resolved";

    private final FileFinder fileFinder;
    private final PackageResolvedExtractor packageResolvedExtractor;

    private File foundPackageResolvedFile;

    public SwiftPackageResolvedDetectable(DetectableEnvironment environment, FileFinder fileFinder, PackageResolvedExtractor packageResolvedExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.packageResolvedExtractor = packageResolvedExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.eitherFile(PACKAGE_SWIFT_FILENAME, PACKAGE_RESOLVED_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        foundPackageResolvedFile = requirements.file(PACKAGE_RESOLVED_FILENAME, () -> new PackageResolvedNotFoundDetectableResult(environment.getDirectory().getAbsolutePath()));
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException, IOException {
        PackageResolvedResult result = packageResolvedExtractor.extract(foundPackageResolvedFile);
        return result.getFailedDetectableResult()
            .map(Extraction::failure)
            .orElse(Extraction.success(new CodeLocation(result.getDependencyGraph(), environment.getDirectory())));
    }

}
