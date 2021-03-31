/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.font;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class OnlineDetectFontLocator implements DetectFontLocator {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DetectFontInstaller detectFontInstaller;
    private final DirectoryManager directoryManager;

    public OnlineDetectFontLocator(DetectFontInstaller detectFontInstaller, DirectoryManager directoryManager) {
        this.detectFontInstaller = detectFontInstaller;
        this.directoryManager = directoryManager;
    }

    @Override
    public File locateRegularFontFile() throws DetectUserFriendlyException {
        return locateFontFile(DetectFontLocator.FONT_FILE_NAME_REGULAR);
    }

    @Override
    public File locateBoldFontFile() throws DetectUserFriendlyException {
        return locateFontFile(DetectFontLocator.FONT_FILE_NAME_BOLD);
    }

    private File locateFontFile(String fontFileName) throws DetectUserFriendlyException {
        try {
            File fontsDirectory = directoryManager.getPermanentDirectory("fonts");
            File fontFile = new File(fontsDirectory, fontFileName);
            logger.debug("Locating font file {}", fontFile.getAbsolutePath());
            if (!fontFile.exists()) {
                detectFontInstaller.installFonts(fontsDirectory);
            }
            return fontFile;
        } catch (Exception e) {
            throw new DetectUserFriendlyException("Unable to install the fonts from Artifactory.", e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}
