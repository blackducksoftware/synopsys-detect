package com.synopsys.integration.detectable.detectables.yarn;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLock;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockResult;

public class YarnPackager {
    private final Gson gson;
    private final YarnLockParser yarnLockParser;

    public YarnPackager(Gson gson, YarnLockParser yarnLockParser) {
        this.gson = gson;
        this.yarnLockParser = yarnLockParser;
    }

    public YarnLockResult generateYarnResult(File packageJsonFile, File yarnLockFile) throws IOException {
        String packageJsonText = FileUtils.readFileToString(packageJsonFile, StandardCharsets.UTF_8);
        PackageJson packageJson = gson.fromJson(packageJsonText, PackageJson.class);

        List<String> yarnLockLines = FileUtils.readLines(yarnLockFile, StandardCharsets.UTF_8);
        YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockLines);

        return new YarnLockResult(packageJson, yarnLockFile, yarnLock);
    }
}
