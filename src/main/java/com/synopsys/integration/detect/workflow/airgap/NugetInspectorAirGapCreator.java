package com.synopsys.integration.detect.workflow.airgap;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.tool.detector.inspector.nuget.AirgapNugetInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspector.nuget.ArtifactoryNugetInspectorInstaller;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class NugetInspectorAirGapCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArtifactoryNugetInspectorInstaller inspectorInstaller;

    public NugetInspectorAirGapCreator(ArtifactoryNugetInspectorInstaller inspectorInstaller) {
        this.inspectorInstaller = inspectorInstaller;
    }

    public void installDependencies(File installFolder) throws DetectUserFriendlyException {
        logger.info("Installing detect nuget inspector for linux.");
        install(installFolder, AirgapNugetInspectorResolver.LINUX_DIR, ArtifactoryConstants.NUGET_INSPECTOR_LINUX_PROPERTY);

        logger.info("Installing detect nuget inspector for windows.");
        install(installFolder, AirgapNugetInspectorResolver.WINDOWS_DIR, ArtifactoryConstants.NUGET_INSPECTOR_WINDOWS_PROPERTY);

        logger.info("Installing detect nuget inspector for mac.");
        install(installFolder, AirgapNugetInspectorResolver.MAC_DIR, ArtifactoryConstants.NUGET_INSPECTOR_MAC_PROPERTY);
    }

    private void install(File container, String targetDirectory, String propertyName) throws DetectUserFriendlyException {
        try {
            File destination = new File(container, targetDirectory);
            File downloaded = inspectorInstaller.downloadZip(propertyName, destination); // actually downloads it to 'destination/zipName'
            FileUtils.copyDirectory(downloaded, destination); //move 'destination/zipName' to 'destination'
            FileUtils.deleteDirectory(downloaded); //delete 'destination/zipName'
        } catch (DetectableException | IOException e) {
            logger.warn("An error occurred installing project-inspector to the " + targetDirectory + " folder.");
            throw new DetectUserFriendlyException("An error occurred installing project-inspector to the " + targetDirectory + " folder.", e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}
