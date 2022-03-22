package com.synopsys.integration.detectable.detectables.xcode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.detectable.result.PackageResolvedNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectables.swift.cli.SwiftCliDetectable;
import com.synopsys.integration.detectable.detectables.swift.lock.PackageResolvedExtractor;
import com.synopsys.integration.detectable.detectables.swift.lock.SwiftPackageResolvedDetectable;
import com.synopsys.integration.detectable.detectables.xcode.model.XcodeFileReference;
import com.synopsys.integration.detectable.detectables.xcode.model.XcodeWorkspace;
import com.synopsys.integration.detectable.detectables.xcode.parse.XcodeWorkspaceFormatChecker;
import com.synopsys.integration.detectable.detectables.xcode.parse.XcodeWorkspaceParser;
import com.synopsys.integration.detectable.extraction.Extraction;

public class XcodeWorkspaceExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final XcodeWorkspaceParser xcodeWorkspaceParser;
    private final XcodeWorkspaceFormatChecker xcodeWorkspaceFormatChecker;
    private final PackageResolvedExtractor packageResolvedExtractor;
    private final FileFinder fileFinder;

    public XcodeWorkspaceExtractor(
        XcodeWorkspaceParser xcodeWorkspaceParser,
        XcodeWorkspaceFormatChecker xcodeWorkspaceFormatChecker,
        PackageResolvedExtractor packageResolvedExtractor, FileFinder fileFinder
    ) {
        this.xcodeWorkspaceParser = xcodeWorkspaceParser;
        this.xcodeWorkspaceFormatChecker = xcodeWorkspaceFormatChecker;
        this.packageResolvedExtractor = packageResolvedExtractor;
        this.fileFinder = fileFinder;
    }

    public List<Extraction> findSubExtractionsInWorkspace(File workspaceDataFile, File workspaceDirectory) throws IOException, ParserConfigurationException, SAXException {
        String workspaceFileContents = FileUtils.readFileToString(workspaceDataFile, Charset.defaultCharset());
        XcodeWorkspace xcodeWorkspace = xcodeWorkspaceParser.parse(workspaceFileContents);
        xcodeWorkspaceFormatChecker.checkForVersionCompatibility(xcodeWorkspace);

        List<Extraction> subExtractions = new LinkedList<>();
        for (XcodeFileReference fileReference : xcodeWorkspace.getFileReferences()) {
            File workspaceSubprojectDirectory = workspaceDirectory.toPath().resolve(fileReference.getRelativeLocation()).toFile();
            if (!workspaceSubprojectDirectory.exists()) {
                logger.warn(
                    "Failed to find subproject '{}' as defined in the workspace at '{}'",
                    workspaceSubprojectDirectory,
                    workspaceDataFile.getParentFile().getAbsolutePath()
                );
                continue;
            }

            switch (fileReference.getFileReferenceType()) {
                case DIRECTORY:
                    File projectDirectory = workspaceDirectory.toPath().resolve(fileReference.getRelativeLocation()).toFile();
                    Extraction packagedResolvedExtraction = extractStandalonePackageResolved(workspaceDirectory, projectDirectory);
                    subExtractions.add(packagedResolvedExtraction);
                    break;
                case XCODE_PROJECT:
                    Extraction xcodeProjectExtraction = extractFromXcodeProject(workspaceDirectory);
                    subExtractions.add(xcodeProjectExtraction);
                    break;
                default:
                    throw new UnsupportedOperationException(String.format("Unrecognized FileReferenceType: %s", fileReference.getFileReferenceType()));
            }
        }

        return subExtractions;
    }

    private Extraction extractStandalonePackageResolved(File workspaceDirectory, File projectDirectory) throws IOException {
        File packageResolved = fileFinder.findFile(projectDirectory, SwiftPackageResolvedDetectable.PACKAGE_RESOLVED_FILENAME);
        if (packageResolved != null) {
            return packageResolvedExtractor.extract(packageResolved, workspaceDirectory);
        } else {
            File swiftFile = fileFinder.findFile(projectDirectory, SwiftCliDetectable.PACKAGE_SWIFT_FILENAME);
            if (swiftFile != null) {
                String failureDescription = new PackageResolvedNotFoundDetectableResult(projectDirectory.getAbsolutePath()).toDescription();
                return new Extraction.Builder().failure(failureDescription).build();
            } else {
                return new Extraction.Builder().failure(String.format(
                    "Failed to find a %s file in the expected location (%s) as defined in the Xcode workspace (%s)",
                    SwiftPackageResolvedDetectable.PACKAGE_RESOLVED_FILENAME,
                    projectDirectory.getAbsolutePath(),
                    workspaceDirectory
                )).build();
            }
        }
    }

    private Extraction extractFromXcodeProject(File workspaceDirectory) throws IOException {
        File searchDirectory = new File(workspaceDirectory, XcodeProjectDetectable.PACKAGE_RESOLVED_RELATIVE_PATH);
        File packageResolved = fileFinder.findFile(searchDirectory, SwiftPackageResolvedDetectable.PACKAGE_RESOLVED_FILENAME);
        if (packageResolved != null) {
            return packageResolvedExtractor.extract(packageResolved, workspaceDirectory);
        } else {
            return new Extraction.Builder().failure(String.format(
                "Failed to find %s file within the Xcode project (%s) as defined in the Xcode workspace (%s)",
                SwiftPackageResolvedDetectable.PACKAGE_RESOLVED_FILENAME,
                searchDirectory.getPath(),
                workspaceDirectory.getAbsolutePath()
            )).build();
        }
    }
}
