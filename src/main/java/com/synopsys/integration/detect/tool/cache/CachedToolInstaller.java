/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.cache;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CachedToolInstaller {
    //    private final Path pathToCachedInspectorFile;
    //    private final Gson gson;
    private JsonObject cachedToolFileData;
    private CachedToolDataKeyMap cachedToolDataKeyMap = new CachedToolDataKeyMap();

    public CachedToolInstaller(Path pathToCachedInspectorFile, Gson gson) {
        //        this.pathToCachedInspectorFile = pathToCachedInspectorFile;
        //        this.gson = gson;
        try {
            String cachedToolFileText = FileUtils.readFileToString(pathToCachedInspectorFile.toFile(), Charset.defaultCharset());
            cachedToolFileData = gson.fromJson(cachedToolFileText, JsonObject.class);
        } catch (IOException e) {
        }
    }

    public Optional<File> installCachedTool(InstalledTool tool) {
        String key = cachedToolDataKeyMap.getKey(tool);
        String cachedToolLocation = cachedToolFileData.getAsJsonObject(key).getAsString();
        File cachedTool = new File(cachedToolLocation);
        if (cachedTool.exists()) {
            return Optional.of(cachedTool);
        }
        return Optional.empty();
    }

}
