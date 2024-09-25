package com.blackduck.integration.detect.workflow.airgap;

import java.io.File;

import com.blackduck.integration.detect.workflow.blackduck.font.DetectFontInstaller;

public class DetectFontAirGapCreator {
    private final DetectFontInstaller detectFontInstaller;

    public DetectFontAirGapCreator(DetectFontInstaller detectFontInstaller) {
        this.detectFontInstaller = detectFontInstaller;
    }

    public void installFonts(File fontDirectory) {
        detectFontInstaller.installFonts(fontDirectory);
    }
}
