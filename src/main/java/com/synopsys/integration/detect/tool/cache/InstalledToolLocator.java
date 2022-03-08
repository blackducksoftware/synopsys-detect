package com.synopsys.integration.detect.tool.cache;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class InstalledToolLocator {
    private InstalledToolData installedToolData;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public InstalledToolLocator(Path pathToInstalledToolDataFile, Gson gson) {
        File installedToolDataFile = pathToInstalledToolDataFile.toFile();
        try {
            if (installedToolDataFile.isDirectory()) {
                // Path is to parent of cached inspector file
                installedToolDataFile = new File(installedToolDataFile, InstalledToolManager.INSTALLED_TOOL_FILE_NAME);
            }
            if (installedToolDataFile.exists()) {
                String installedToolDataFileText = FileUtils.readFileToString(installedToolDataFile, Charset.defaultCharset());
                installedToolData = gson.fromJson(installedToolDataFileText, InstalledToolData.class);
            }
        } catch (Exception e) {
            logger.debug(String.format("Encountered error parsing information on installed tools from file: %s", installedToolDataFile.getAbsolutePath()));
        }
    }

    public Optional<File> locateTool(String toolKey) {
        if (installedToolData != null && installedToolData.toolData != null) {
            String installedToolLocation = installedToolData.toolData.get(toolKey);
            if (installedToolLocation != null) {
                File installedTool = new File(installedToolLocation);
                if (installedTool.exists()) {
                    return Optional.of(installedTool);
                }
            }
        }
        return Optional.empty();
    }

}
