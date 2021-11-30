/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.xcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Swift", forge = "GITHUB", requirementsMarkdown = "Directory: *.xcodeproj, Files: Package.resolved")
public class XcodeSwiftDetectable extends Detectable {
    private static final String PACKAGE_RESOLVED_FILENAME = "Package.resolved";
    private static final Path PACKAGE_RESOLVED_PARENT_PATH = Paths.get("project.xcworkspace/xcshareddata/swiftpm");
    private static final String XCODE_PROJECT_PATTERN = "*.xcodeproj";

    private final FileFinder fileFinder;
    private final XcodeSwiftExtractor xcodeProjectExtractor;

    private File foundPackageResolvedFile;

    public XcodeSwiftDetectable(DetectableEnvironment environment, FileFinder fileFinder, XcodeSwiftExtractor xcodeProjectExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.xcodeProjectExtractor = xcodeProjectExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        File xcodeProject = requirements.directory(XCODE_PROJECT_PATTERN);

        if (requirements.isCurrentlyMet()) {
            File swiftPMDirectory = xcodeProject.toPath().resolve(PACKAGE_RESOLVED_PARENT_PATH).toFile();
            foundPackageResolvedFile = requirements.file(swiftPMDirectory, PACKAGE_RESOLVED_FILENAME);
        }

        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws FileNotFoundException {
        return xcodeProjectExtractor.extract(foundPackageResolvedFile);
    }

}
