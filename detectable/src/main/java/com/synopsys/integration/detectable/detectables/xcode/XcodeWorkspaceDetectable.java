package com.synopsys.integration.detectable.detectables.xcode;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.SearchPattern;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FailedDetectableResult;
import com.synopsys.integration.detectable.detectables.swift.lock.PackageResolvedExtractor;
import com.synopsys.integration.detectable.detectables.swift.lock.model.PackageResolvedResult;
import com.synopsys.integration.detectable.detectables.xcode.model.XcodeWorkspaceResult;
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
        PackageResolvedResult localResult = null;
        if (foundPackageResolvedFile != null) {
            localResult = packageResolvedExtractor.extract(foundPackageResolvedFile);
            Optional<FailedDetectableResult> failedDetectableResult = localResult.getFailedDetectableResult();
            if (failedDetectableResult.isPresent()) {
                return Extraction.failure(failedDetectableResult.get());
            }
        }

        XcodeWorkspaceResult xcodeWorkspaceResult = null;
        if (foundWorkspaceDataFile != null) {
            xcodeWorkspaceResult = xcodeWorkspaceExtractor.extract(foundWorkspaceDataFile, workspaceDirectory);
            if (xcodeWorkspaceResult.isFailure()) {
                return Extraction.failure(xcodeWorkspaceResult.getFailedDetectableResults());
            }
        }

        DependencyGraph dependencyGraph = new BasicDependencyGraph();
        if (xcodeWorkspaceResult != null && xcodeWorkspaceResult.getDependencyGraph() != null) {
            dependencyGraph.copyGraphToRoot(xcodeWorkspaceResult.getDependencyGraph());
        }
        if (localResult != null) {
            dependencyGraph.copyGraphToRoot(localResult.getDependencyGraph());
        }

        return Extraction.success(new CodeLocation(dependencyGraph, environment.getDirectory()));
    }

}
