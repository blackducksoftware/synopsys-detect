/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.airgap;

import java.io.File;

import com.synopsys.integration.detect.workflow.blackduck.font.DetectFontInstaller;

public class DetectFontAirGapCreator {
    private DetectFontInstaller detectFontInstaller;

    public DetectFontAirGapCreator(DetectFontInstaller detectFontInstaller) {
        this.detectFontInstaller = detectFontInstaller;
    }

    public void installFonts(File fontDirectory) {
        detectFontInstaller.installFonts(fontDirectory);
    }
}
