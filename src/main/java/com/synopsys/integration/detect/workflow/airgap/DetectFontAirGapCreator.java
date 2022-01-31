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
