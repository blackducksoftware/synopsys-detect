package com.synopsys.integration.detectable.detectables.xcode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.detectables.xcode.model.XcodeFileReference;
import com.synopsys.integration.detectable.detectables.xcode.model.XcodeWorkspace;
import com.synopsys.integration.detectable.detectables.xcode.parse.XcodeWorkspaceFormatChecker;
import com.synopsys.integration.detectable.detectables.xcode.parse.XcodeWorkspaceParser;
import com.synopsys.integration.detectable.extraction.Extraction;

public class XcodeWorkspaceExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final XcodeWorkspaceParser xcodeWorkspaceParser;
    private final XcodeWorkspaceFormatChecker xcodeWorkspaceFormatChecker;
    private final XcodePackageResolvedExtractor xcodePackageResolvedExtractor;
    private final FileFinder fileFinder;

    public XcodeWorkspaceExtractor(
        XcodeWorkspaceParser xcodeWorkspaceParser,
        XcodeWorkspaceFormatChecker xcodeWorkspaceFormatChecker,
        XcodePackageResolvedExtractor xcodePackageResolvedExtractor, FileFinder fileFinder
    ) {
        this.xcodeWorkspaceParser = xcodeWorkspaceParser;
        this.xcodeWorkspaceFormatChecker = xcodeWorkspaceFormatChecker;
        this.xcodePackageResolvedExtractor = xcodePackageResolvedExtractor;
        this.fileFinder = fileFinder;
    }

    public Extraction extract(File workspaceDataFile, File workspaceBaseDirectory) throws IOException, ParserConfigurationException, SAXException {
        String workspaceFileContents = FileUtils.readFileToString(workspaceDataFile, Charset.defaultCharset());
        XcodeWorkspace xcodeWorkspace = xcodeWorkspaceParser.parse(workspaceFileContents);
        checkFileFormat(xcodeWorkspace);

        for (XcodeFileReference fileReference : xcodeWorkspace.getFileReferences()) {
            File workspaceSubprojectDirectory = workspaceBaseDirectory.toPath().resolve(fileReference.getRelativeLocation()).toFile();
            if (!workspaceSubprojectDirectory.exists()) {
                logger.warn(
                    "Failed to find subproject '{}' in defined in the workspace at '{}'",
                    workspaceSubprojectDirectory,
                    workspaceDataFile.getParentFile().getAbsolutePath()
                );
                continue;
            }

            switch (fileReference.getFileReferenceType()) {
                case DIRECTORY:
                    // TODO: Run SwiftPackageResolvedDetectable (doesn't exist)
                    // TODO: Should Detectables be able to recommend other Detectables be run?
                    break;
                case XCODE_PROJECT:
                    File searchDirectory = new File(workspaceBaseDirectory, XcodeProjectDetectable.PACKAGE_RESOLVED_RELATIVE_PATH);
                    fileFinder.findFile(searchDirectory, XcodeProjectDetectable.PACKAGE_RESOLVED_RELATIVE_PATH);
                    Extraction extraction = xcodePackageResolvedExtractor.extract(workspaceSubprojectDirectory, workspaceDataFile.getParentFile());
                    break;
                default:
                    throw new UnsupportedOperationException(String.format("Unrecognized FileReferenceType: %s", fileReference.getFileReferenceType()));
            }
        }

        return null; // TODO: Return extraction
    }

    private void checkFileFormat(XcodeWorkspace xcodeWorkspace) {
        xcodeWorkspaceFormatChecker.checkForVersionCompatibility(
            xcodeWorkspace,
            (fileFormatVersion, knownVersions) -> logger.warn(String.format(
                "The format version of Package.resolved (%s) is unknown to Detect, but processing will continue. Known format versions are (%s).",
                fileFormatVersion,
                StringUtils.join(knownVersions, ", ")
            ))
        );
    }
}
