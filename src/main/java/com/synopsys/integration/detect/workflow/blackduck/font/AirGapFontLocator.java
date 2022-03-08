package com.synopsys.integration.detect.workflow.blackduck.font;

import java.io.File;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;

public class AirGapFontLocator implements DetectFontLocator {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AirGapInspectorPaths airGapPaths;

    public AirGapFontLocator(AirGapInspectorPaths airGapPaths) {
        this.airGapPaths = airGapPaths;
    }

    @Override
    public File locateRegularFontFile() throws DetectUserFriendlyException {
        return locateFontFile(DetectFontLocator.DEFAULT_FONT_FILE_NAME_REGULAR, DetectFontLocator.CUSTOM_FONT_FILE_DIR_NAME_REGULAR);
    }

    @Override
    public File locateBoldFontFile() throws DetectUserFriendlyException {
        return locateFontFile(DetectFontLocator.DEFAULT_FONT_FILE_NAME_BOLD, DetectFontLocator.CUSTOM_FONT_FILE_DIR_NAME_BOLD);
    }

    private File locateFontFile(String defaultFontFileName, String customFontDirectoryName) throws DetectUserFriendlyException {
        File fontFile = airGapPaths.getFontsAirGapDirectory()
            .map(fontAirGapDirectory -> locateFontFile(fontAirGapDirectory, defaultFontFileName, customFontDirectoryName))
            .orElseThrow(() -> new DetectUserFriendlyException(
                String.format("Could not get the font file %s from the air gap path", defaultFontFileName),
                ExitCodeType.FAILURE_GENERAL_ERROR
            ));
        logger.debug("Locating font file {}", fontFile.getAbsolutePath());

        return fontFile;
    }

    @Nullable
    private File locateFontFile(File fontsDirectory, String defaultFontFileName, String customFontDirectoryName) {
        File customFontDirectory = new File(fontsDirectory, customFontDirectoryName);
        if (customFontDirectory.exists() && customFontDirectory.isDirectory() && customFontDirectory.listFiles() != null && customFontDirectory.listFiles().length != 0) {
            // If custom font directory is present and non-empty, use first file in that directory
            for (File file : customFontDirectory.listFiles()) {
                if (file.getName().endsWith(DetectFontLocator.TTF_FILE_EXTENSION)) {
                    return file;
                }
            }
        }
        return new File(fontsDirectory, defaultFontFileName);
    }
}
