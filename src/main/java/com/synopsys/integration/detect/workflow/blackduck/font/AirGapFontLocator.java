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
import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;

public class AirGapFontLocator implements DetectFontLocator {
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
        return airGapPaths.getNugetInspectorAirGapFile()
                   .map(fontAirGapPath -> new File(fontAirGapPath, childName))
                   .orElseThrow(() -> new DetectUserFriendlyException(String.format("Could not get the font file %s from the air gap path", childName), ExitCodeType.FAILURE_GENERAL_ERROR));
    }
}
