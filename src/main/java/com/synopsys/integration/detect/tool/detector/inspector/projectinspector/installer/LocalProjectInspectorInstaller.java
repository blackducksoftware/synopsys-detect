package com.synopsys.integration.detect.tool.detector.inspector.projectinspector.installer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.detector.inspector.projectinspector.ProjectInspectorExecutableLocator;
import com.synopsys.integration.detect.util.DetectZipUtil;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class LocalProjectInspectorInstaller implements ProjectInspectorInstaller {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProjectInspectorExecutableLocator projectInspectorExecutableLocator;
    private final Path projectInspectorZipPath;

    public LocalProjectInspectorInstaller(
        ProjectInspectorExecutableLocator projectInspectorExecutableLocator,
        Path projectInspectorZipPath
    ) {
        this.projectInspectorExecutableLocator = projectInspectorExecutableLocator;
        this.projectInspectorZipPath = projectInspectorZipPath;
    }

    @Override
    @Nullable
    public File install(File destDirectory) throws DetectableException {
        logger.debug("Extracting local project inspector zip.");
        try {
            File localInspectorZipFile = findFile(projectInspectorZipPath);
            DetectZipUtil.unzip(localInspectorZipFile, destDirectory, Charset.defaultCharset());
        } catch (IOException e) {
            logger.trace("Exception extracting:", e);
            throw new DetectableException("Failed to unzip artifact: " + projectInspectorZipPath, e);
        }
        return projectInspectorExecutableLocator.findExecutable(destDirectory);
    }

    @Override
    public boolean shouldFallbackToPreviousInstall() {
        return false;
    }

    private File findFile(Path localProjectInspectorPath) throws DetectableException {
        logger.debug("Using user-provided project inspector zip path: {}", localProjectInspectorPath);
        File providedZipCandidate = localProjectInspectorPath.toFile();
        if (providedZipCandidate.isFile()) {
            logger.debug("Found user-specified project inspector zip: {}", providedZipCandidate.getAbsolutePath());
            return providedZipCandidate;
        } else {
            String msg = String.format("Provided Project Inspector zip path (%s) does not exist or is not a file", providedZipCandidate.getAbsolutePath());
            logger.debug(msg);
            throw new DetectableException(msg);
        }
    }
}
