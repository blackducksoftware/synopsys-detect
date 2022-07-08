package com.synopsys.integration.detect.tool.detector.inspectors.projectinspector;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.util.DetectZipUtil;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class LocalProjectInspectorInstaller {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProjectInspectorExecutableLocator projectInspectorExecutableLocator;

    public LocalProjectInspectorInstaller(final ProjectInspectorExecutableLocator projectInspectorExecutableLocator) {
        this.projectInspectorExecutableLocator = projectInspectorExecutableLocator;
    }

    @Nullable
    public File install(File destDirectory, File localProjectInspectorZipFile) throws DetectableException {
        logger.debug("Extracting local project inspector zip.");
        try {
            DetectZipUtil.unzip(localProjectInspectorZipFile, destDirectory, Charset.defaultCharset());
        } catch (IOException e) {
            logger.trace("Exception extracting:", e);
            throw new DetectableException("Failed to unzip artifact: " + localProjectInspectorZipFile, e);
        }
        return projectInspectorExecutableLocator.findExecutable(destDirectory);
    }
}
