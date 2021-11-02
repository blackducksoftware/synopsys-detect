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

public class CachedToolInstaller {
    private JsonObject cachedToolFileData;
    private CachedToolFileData cachedToolDataKeyMap = new CachedToolFileData();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public CachedToolInstaller(Path pathToCachedInspectorFile, Gson gson) {
        File cachedInspectorFile = pathToCachedInspectorFile.toFile();
        try {
            if (!cachedInspectorFile.getName().equals(CachedToolFileData.CACHED_TOOL_FILE_NAME)) {
                // Path is to parent of cached inspector file
                cachedInspectorFile = new File(cachedInspectorFile, CachedToolFileData.CACHED_TOOL_FILE_NAME);
            }
            String cachedToolFileText = FileUtils.readFileToString(cachedInspectorFile, Charset.defaultCharset());
            cachedToolFileData = gson.fromJson(cachedToolFileText, JsonObject.class);
        } catch (Exception e) {
            logger.debug(String.format("Encountered error parsing information on cached tools from file: %s", cachedInspectorFile.getAbsolutePath()));
        }
    }

    public Optional<File> installCachedTool(InstalledTool tool) {
        String key = cachedToolDataKeyMap.getToolJsonKey(tool);
        String cachedToolLocation = cachedToolFileData.get(key).getAsString();
        File cachedTool = new File(cachedToolLocation);
        if (cachedTool.exists()) {
            return Optional.of(cachedTool);
        }
        return Optional.empty();
    }

}
