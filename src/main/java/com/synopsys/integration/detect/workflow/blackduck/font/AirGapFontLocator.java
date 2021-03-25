/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.font;

import java.io.File;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.airgap.AirGapPathFinder;

public class AirGapFontLocator implements DetectFontLocator {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AirGapFontLocator() {
    }

    private Path determineInspectorAirGapPath(File detectJar, AirGapPathFinder airGapPathFinder, String inspectorName) {
        if (detectJar != null) {
            try {
                return airGapPathFinder.createRelativeFontsFile(detectJar.getParentFile()).toPath();
            } catch (Exception e) {
                logger.debug("Exception encountered when guessing air gap path for fonts, returning the detect property instead");
                logger.debug(e.getMessage());
            }
        }
        return airGapPathFinder.createRelativeFontsFile(new File(".")).toPath();
    }

    @Override
    public File locateRegularFontFile() throws DetectUserFriendlyException {
        return null;
    }

    @Override
    public File locateBoldFontFile() throws DetectUserFriendlyException {
        return null;
    }
}
