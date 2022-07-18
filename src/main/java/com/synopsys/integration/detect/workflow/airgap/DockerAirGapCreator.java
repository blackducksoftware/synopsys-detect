package com.synopsys.integration.detect.workflow.airgap;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.tool.detector.inspector.DockerInspectorInstaller;
import com.synopsys.integration.exception.IntegrationException;

public class DockerAirGapCreator {
    private final DockerInspectorInstaller dockerInspectorInstaller;

    public DockerAirGapCreator(DockerInspectorInstaller dockerInspectorInstaller) {
        this.dockerInspectorInstaller = dockerInspectorInstaller;
    }

    public void installDockerDependencies(File dockerFolder) throws DetectUserFriendlyException {
        try {
            File dockerZip = dockerInspectorInstaller.installAirGap(dockerFolder);
            ZipUtil.unpack(dockerZip, dockerFolder);
            FileUtils.deleteQuietly(dockerZip);
        } catch (IntegrationException | IOException e) {
            throw new DetectUserFriendlyException("An error occurred installing docker inspector.", e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}
