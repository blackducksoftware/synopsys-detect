package com.synopsys.integration.detectable.detectables.xcode;

import java.io.File;
import java.io.IOException;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.swift.lock.PackageResolvedExtractor;
import com.synopsys.integration.detectable.detectables.swift.lock.SwiftPackageResolvedDetectable;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Swift", forge = "GITHUB", requirementsMarkdown = "Directory: *.xcodeproj, Files: Package.resolved")
public class XcodeProjectDetectable extends Detectable {
    public static final String PACKAGE_RESOLVED_RELATIVE_PATH = "project.xcworkspace/xcshareddata/swiftpm";

    private final FileFinder fileFinder;
    private final PackageResolvedExtractor packageResolvedExtractor;

    private File foundCodeLocationFile;
    private File foundPackageResolvedFile;

    public XcodeProjectDetectable(DetectableEnvironment environment, FileFinder fileFinder, PackageResolvedExtractor packageResolvedExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.packageResolvedExtractor = packageResolvedExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        foundCodeLocationFile = requirements.directory("*.xcodeproj");

        if (requirements.isCurrentlyMet()) {
            File swiftPMDirectory = new File(foundCodeLocationFile, PACKAGE_RESOLVED_RELATIVE_PATH);
            foundPackageResolvedFile = requirements.file(swiftPMDirectory, SwiftPackageResolvedDetectable.PACKAGE_RESOLVED_FILENAME);
        }

        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws IOException {
        return packageResolvedExtractor.extract(foundPackageResolvedFile, foundCodeLocationFile);
    }

}
