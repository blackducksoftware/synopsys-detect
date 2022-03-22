package com.synopsys.integration.detectable.detectables.xcode;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
import com.synopsys.integration.detectable.detectables.swift.lock.PackageResolvedExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Swift", forge = "GITHUB", requirementsMarkdown = "Directory: *.xcworkspace")
public class XcodeWorkspaceDetectable extends Detectable {
    private final FileFinder fileFinder;
    private final PackageResolvedExtractor packageResolvedExtractor;
    private final XcodeWorkspaceExtractor xcodeWorkspaceExtractor;

    private File workspaceDirectory;
    @Nullable
    private File foundPackageResolvedFile;
    @Nullable
    private File foundWorkspaceDataFile;

    public XcodeWorkspaceDetectable(
        DetectableEnvironment environment,
        FileFinder fileFinder,
        PackageResolvedExtractor packageResolvedExtractor,
        XcodeWorkspaceExtractor xcodeWorkspaceExtractor
    ) {
        super(environment);
        this.fileFinder = fileFinder;
        this.packageResolvedExtractor = packageResolvedExtractor;
        this.xcodeWorkspaceExtractor = xcodeWorkspaceExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        workspaceDirectory = requirements.directory("*.xcworkspace");
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        File swiftPMDirectory = workspaceDirectory.toPath().resolve("xcshareddata/swiftpm").toFile();
        requirements.anyFile(
            new SearchPattern(swiftPMDirectory, "Package.resolved", packageResolvedFile -> foundPackageResolvedFile = packageResolvedFile),
            new SearchPattern(workspaceDirectory, "contents.xcworkspacedata", workspaceDataFile -> foundWorkspaceDataFile = workspaceDataFile)
        );

        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws IOException, ParserConfigurationException, SAXException {
        Extraction localExtraction = new Extraction.Builder().success().build();
        if (foundPackageResolvedFile != null) {
            localExtraction = packageResolvedExtractor.extract(foundPackageResolvedFile, workspaceDirectory);
        }
        if (foundWorkspaceDataFile != null) {
            List<Extraction> workspaceExtractions = xcodeWorkspaceExtractor.findSubExtractionsInWorkspace(foundWorkspaceDataFile, workspaceDirectory);
            // TODO: Combine extractions
        }

        return localExtraction;
    }

}
