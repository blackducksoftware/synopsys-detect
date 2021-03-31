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
import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;

public class AirGapFontLocator implements DetectFontLocator {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AirGapInspectorPaths airGapPaths;

    public AirGapFontLocator(AirGapInspectorPaths airGapPaths) {
        this.airGapPaths = airGapPaths;
    }

    @Override
    public File locateRegularFontFile() throws DetectUserFriendlyException {
        return locateFontFile(DetectFontLocator.FONT_FILE_NAME_REGULAR);
    }

    @Override
    public File locateBoldFontFile() throws DetectUserFriendlyException {
        return locateFontFile(DetectFontLocator.FONT_FILE_NAME_BOLD);
    }

    private File locateFontFile(String childName) throws DetectUserFriendlyException {
        File fontFile = airGapPaths.getFontsAirGapFile()
                            .map(fontAirGapPath -> new File(fontAirGapPath, childName))
                            .orElseThrow(() -> new DetectUserFriendlyException(String.format("Could not get the font file %s from the air gap path", childName), ExitCodeType.FAILURE_GENERAL_ERROR));
        logger.debug("Locating font file {}", fontFile.getAbsolutePath());
        return fontFile;
    }
}
