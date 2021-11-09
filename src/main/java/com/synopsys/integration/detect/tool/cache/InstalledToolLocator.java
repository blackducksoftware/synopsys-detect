/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.cache;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class InstalledToolLocator {
    private JsonObject installedToolData;
    private InstalledToolFileData installedToolFileData = new InstalledToolFileData();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public InstalledToolLocator(Path pathToInstalledToolDataFile, Gson gson) {
        File installedToolDataFile = pathToInstalledToolDataFile.toFile();
        try {
            if (!installedToolDataFile.getName().equals(InstalledToolFileData.INSTALLED_TOOL_FILE_NAME)) {
                // Path is to parent of cached inspector file
                installedToolDataFile = new File(installedToolDataFile, InstalledToolFileData.INSTALLED_TOOL_FILE_NAME);
            }
            String installedToolDataFileText = FileUtils.readFileToString(installedToolDataFile, Charset.defaultCharset());
            installedToolData = gson.fromJson(installedToolDataFileText, JsonObject.class);
        } catch (Exception e) {
            logger.debug(String.format("Encountered error parsing information on installed tools from file: %s", installedToolDataFile.getAbsolutePath()));
        }
    }

    public Optional<File> locateTool(InstalledTool tool) {
        String key = installedToolFileData.getToolJsonKey(tool);
        String installedToolLocation = installedToolData.get(key).getAsString();
        File installedTool = new File(installedToolLocation);
        if (installedTool.exists()) {
            return Optional.of(installedTool);
        }
        return Optional.empty();
    }

}
