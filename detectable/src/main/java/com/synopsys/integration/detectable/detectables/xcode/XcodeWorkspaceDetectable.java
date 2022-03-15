package com.synopsys.integration.detectable.detectables.xcode;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.SearchPattern;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Swift", forge = "GITHUB", requirementsMarkdown = "Directory: *.xcodeproj, Files: Package.resolved")
public class XcodeWorkspaceDetectable extends Detectable {
    private final FileFinder fileFinder;
    private final XcodePackageResolvedExtractor xcodePackageResolvedExtractor;
    private final XcodeWorkspaceExtractor xcodeWorkspaceExtractor;

    private File workspaceDirectory;
    @Nullable
    private File foundPackageResolvedFile;
    private File foundWorkspaceDataFile;

    public XcodeWorkspaceDetectable(
        DetectableEnvironment environment,
        FileFinder fileFinder,
        XcodePackageResolvedExtractor xcodePackageResolvedExtractor,
        XcodeWorkspaceExtractor xcodeWorkspaceExtractor
    ) {
        super(environment);
        this.fileFinder = fileFinder;
        this.xcodePackageResolvedExtractor = xcodePackageResolvedExtractor;
        this.xcodeWorkspaceExtractor = xcodeWorkspaceExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements workspaceRequirements = new Requirements(fileFinder, environment);
        workspaceDirectory = workspaceRequirements.directory("*.xcworkspace");
        if (workspaceRequirements.isAlreadyFailed()) {
            return workspaceRequirements.result();
        }

        File swiftPMDirectory = workspaceDirectory.toPath().resolve("xcshareddata/swiftpm").toFile();

        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.anyFile(
            new SearchPattern(swiftPMDirectory, "Package.resolved", packageResolvedFile -> foundPackageResolvedFile = packageResolvedFile),
            new SearchPattern(workspaceDirectory, "contents.xcworkspacedata", workspaceDataFile -> foundWorkspaceDataFile = workspaceDataFile)
        );

        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws IOException, ParserConfigurationException, SAXException {
        Extraction localPackages = xcodePackageResolvedExtractor.extract(foundPackageResolvedFile, workspaceDirectory);
        Extraction workspaceExtraction = xcodeWorkspaceExtractor.extract(foundWorkspaceDataFile, environment.getDirectory());
        // TODO: Combine extractions?
        return localPackages;
    }

}
