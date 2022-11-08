package com.synopsys.integration.detect.tool.detector.inspector.nuget;

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.util.OperatingSystemType;

public class NugetInspectorExecutableLocator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectInfo detectInfo;

    public NugetInspectorExecutableLocator(DetectInfo detectInfo) {
        this.detectInfo = detectInfo;
    }

    @Nullable
    public File findExecutable(File extractedZip) throws DetectableException {
        return findExecutable(extractedZip, determineExecutableNameFromOS());
    }

    @NotNull
    public String determineExecutableNameFromOS() {
        if (detectInfo.getCurrentOs() == OperatingSystemType.WINDOWS) {
            return "detect-nuget-inspector.exe";
        } else {
            return "detect-nuget-inspector";
        }
    }

    @Nullable
    public File findExecutable(File extractedZip, String executableName) throws DetectableException {
        logger.debug("Looking for '" + executableName + "' in " + extractedZip.toString());
        File executable = new File(extractedZip, executableName);
        if (executable.exists()) {
            logger.debug("Found it: " + executable);
            if (!executable.canExecute() && !executable.setExecutable(true)) {
                throw new DetectableException("Unable to set project inspector to executable: " + executable);
            }
            return executable;
        } else {
            logger.debug("Could not find executable: " + executable);
            return null;
        }
    }
}
