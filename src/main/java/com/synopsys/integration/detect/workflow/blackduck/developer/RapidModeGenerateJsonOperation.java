/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.file.DetectFileUtils;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class RapidModeGenerateJsonOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Gson gson;
    private final DirectoryManager directoryManager;

    public RapidModeGenerateJsonOperation(Gson gson, DirectoryManager directoryManager) {
        this.gson = gson;
        this.directoryManager = directoryManager;
    }

    public File generateJsonFile(NameVersion projectNameVersion, List<DeveloperScanComponentResultView> results) throws DetectUserFriendlyException {
        IntegrationEscapeUtil escapeUtil = new IntegrationEscapeUtil();
        String escapedProjectName = escapeUtil.replaceWithUnderscore(projectNameVersion.getName());
        String escapedProjectVersionName = escapeUtil.replaceWithUnderscore(projectNameVersion.getVersion());
        File jsonScanFile = new File(directoryManager.getScanOutputDirectory(), escapedProjectName + "_" + escapedProjectVersionName + "_BlackDuck_DeveloperMode_Result.json");
        if (jsonScanFile.exists()) {
            try {
                Files.delete(jsonScanFile.toPath());
            } catch (IOException ex) {
                logger.warn(String.format("Unable to delete an already-existing Black Duck Rapid Scan Result file: %s", jsonScanFile.getAbsoluteFile()));
                new Slf4jIntLogger(logger).error(ex); //TODO: Uhm, ew. - jp
            }
        }

        String jsonString = gson.toJson(results);
        logger.trace("Rapid Scan JSON result output: ");
        logger.trace(String.format("%s", jsonString));
        try {
            DetectFileUtils.writeToFile(jsonScanFile, jsonString);
        } catch (IOException ex) {
            throw new DetectUserFriendlyException("Cannot create rapid scan output file", ex, ExitCodeType.FAILURE_UNKNOWN_ERROR);
        }
        return jsonScanFile;
    }
}
