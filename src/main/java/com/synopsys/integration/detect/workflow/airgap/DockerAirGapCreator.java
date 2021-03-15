/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.airgap;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.tool.detector.inspectors.DockerInspectorInstaller;
import com.synopsys.integration.exception.IntegrationException;

public class DockerAirGapCreator {
    private final DockerInspectorInstaller dockerInspectorInstaller;

    public DockerAirGapCreator(final DockerInspectorInstaller dockerInspectorInstaller) {
        this.dockerInspectorInstaller = dockerInspectorInstaller;
    }

    public void installDockerDependencies(final File dockerFolder) throws DetectUserFriendlyException {
        try {
            final File dockerZip = dockerInspectorInstaller.installAirGap(dockerFolder);
            ZipUtil.unpack(dockerZip, dockerFolder);
            FileUtils.deleteQuietly(dockerZip);
        } catch (final IntegrationException | IOException e) {
            throw new DetectUserFriendlyException("An error occurred installing docker inspector.", e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}
