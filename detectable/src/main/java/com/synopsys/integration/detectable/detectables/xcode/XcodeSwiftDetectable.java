package com.synopsys.integration.detectable.detectables.xcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;

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

    private final FileFinder fileFinder;
    private final XcodeSwiftExtractor xcodeProjectExtractor;

    private File foundCodeLocationFile;
    private File foundPackageResolvedFile;

    public XcodeSwiftDetectable(DetectableEnvironment environment, FileFinder fileFinder, XcodeSwiftExtractor xcodeProjectExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.xcodeProjectExtractor = xcodeProjectExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements xcodeWorkspaceRequirements = createRequirements("*.xcworkspace", Paths.get("xcshareddata/swiftpm"), (foundDirectory, foundPackageResolved) -> {
            foundCodeLocationFile = foundDirectory;
            foundPackageResolvedFile = foundPackageResolved;
        });
        if (xcodeWorkspaceRequirements.isCurrentlyMet()) {
            return xcodeWorkspaceRequirements.result();
        }

        Requirements xcodeProjectRequirements = createRequirements("*.xcodeproj", Paths.get("project.xcworkspace/xcshareddata/swiftpm"), (foundDirectory, foundPackageResolved) -> {
            foundCodeLocationFile = foundDirectory;
            foundPackageResolvedFile = foundPackageResolved;
        });
        if (xcodeProjectRequirements.isCurrentlyMet()) {
            return xcodeProjectRequirements.result();
        }

        // TODO: Would like to say "Could not find xcode workspace or xcode project" maybe with combined DetectableResults???
        return xcodeProjectRequirements.result(); // TODO: Maybe DetectableResultJoiner::joinForSuccess should exist? If either result.isPassed() == true return new DetectableResult(passed = true)
    }

    private Requirements createRequirements(String directoryPattern, Path packageResolvedRelativePath, BiConsumer<File, File> foundPackageResolvedConsumer) {
        Requirements requirements = new Requirements(fileFinder, environment);
        File foundDirectory = requirements.directory(directoryPattern);

        if (requirements.isCurrentlyMet()) {
            File swiftPMDirectory = foundDirectory.toPath().resolve(packageResolvedRelativePath).toFile();
            File foundPackageResolved = requirements.file(swiftPMDirectory, PACKAGE_RESOLVED_FILENAME);
            foundPackageResolvedConsumer.accept(foundDirectory, foundPackageResolved);
        }

        return requirements;
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws FileNotFoundException {
        return xcodeProjectExtractor.extract(foundPackageResolvedFile, foundCodeLocationFile);
    }

}
