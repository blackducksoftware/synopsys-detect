/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.font;

import java.io.File;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class OnlineDetectFontLocator implements DetectFontLocator {
    public static final String FONT_FILE_NAME_REGULAR = "NotoSansCJKtc-Regular.ttf";
    public static final String FONT_FILE_NAME_BOLD = "NotoSansCJKtc-Bold.ttf";

    private final DetectFontInstaller detectFontInstaller;
    private final DirectoryManager directoryManager;

    public OnlineDetectFontLocator(DetectFontInstaller detectFontInstaller, DirectoryManager directoryManager) {
        this.detectFontInstaller = detectFontInstaller;
        this.directoryManager = directoryManager;
    }

    @Override
    public File locateRegularFontFile() throws DetectUserFriendlyException {
        return locateFontFile(FONT_FILE_NAME_REGULAR);
    }

    @Override
    public File locateBoldFontFile() throws DetectUserFriendlyException {
        return locateFontFile(FONT_FILE_NAME_BOLD);
    }

    private File locateFontFile(String fontName) throws DetectUserFriendlyException {
        try {
            File nugetDirectory = directoryManager.getPermanentDirectory("fonts");
            return detectFontInstaller.installFonts(nugetDirectory);
        } catch (Exception e) {
            throw new DetectUserFriendlyException("Unable to install the fonts from Artifactory.", e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}
