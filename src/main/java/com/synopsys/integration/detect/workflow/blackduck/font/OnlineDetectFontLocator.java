package com.synopsys.integration.detect.workflow.blackduck.font;

import java.io.File;

import org.jetbrains.annotations.Nullable;
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
        return locateFontFile(DetectFontLocator.DEFAULT_FONT_FILE_NAME_REGULAR, DetectFontLocator.CUSTOM_FONT_FILE_DIR_NAME_REGULAR);
    }

    @Override
    public File locateBoldFontFile() throws DetectUserFriendlyException {
        return locateFontFile(DetectFontLocator.DEFAULT_FONT_FILE_NAME_BOLD, DetectFontLocator.CUSTOM_FONT_FILE_DIR_NAME_BOLD);
    }

    private File locateFontFile(String fontFileName, String customFontDirectoryName) throws DetectUserFriendlyException {
        try {
            File fontsDirectory = directoryManager.getPermanentDirectory("fonts");
            File customFontFile = locateCustomFontFile(fontsDirectory, customFontDirectoryName);
            File fontFile;
            if (customFontFile != null) {
                fontFile = customFontFile;
            } else {
                fontFile = new File(fontsDirectory, fontFileName);
            }
            logger.debug("Locating font file {}", fontFile.getAbsolutePath());
            if (!fontFile.exists()) {
                detectFontInstaller.installFonts(fontsDirectory);
            }
            return fontFile;
        } catch (Exception e) {
            throw new DetectUserFriendlyException("Unable to locate font files or install fonts from Artifactory.", e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    @Nullable
    private File locateCustomFontFile(File fontsDirectory, String customFontDirectoryName) {
        File customFontDirectory = new File(fontsDirectory, customFontDirectoryName);
        if (customFontDirectory.exists() && customFontDirectory.isDirectory() && customFontDirectory.listFiles() != null && customFontDirectory.listFiles().length != 0) {
            // If custom font directory is present and non-empty, use first file in that directory
            for (File file : customFontDirectory.listFiles()) {
                if (file.getName().endsWith(DetectFontLocator.TTF_FILE_EXTENSION)) {
                    return file;
                }
            }
        }
        return null;
    }
}
