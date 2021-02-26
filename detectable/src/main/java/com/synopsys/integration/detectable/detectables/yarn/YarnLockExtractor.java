/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.extraction.Extraction;

public class YarnLockExtractor {
    private final Gson gson;
    private final YarnPackager yarnPackager;

    public YarnLockExtractor(Gson gson, YarnPackager yarnPackager) {
        this.gson = gson;
        this.yarnPackager = yarnPackager;
    }

    public Extraction extract(File sourceDir, File yarnLockFile, File rootPackageJsonFile) {
        try {
            String rootPackageJsonText = FileUtils.readFileToString(rootPackageJsonFile, StandardCharsets.UTF_8);
            PackageJson rootPackageJson = gson.fromJson(rootPackageJsonText, PackageJson.class);
            List<String> yarnLockLines = FileUtils.readLines(yarnLockFile, StandardCharsets.UTF_8);
            YarnResult yarnResult = yarnPackager.generateYarnResult(rootPackageJson,
                yarnLockLines, yarnLockFile.getAbsolutePath(), new ArrayList<>());

            if (yarnResult.getException().isPresent()) {
                throw yarnResult.getException().get();
            }

            return new Extraction.Builder()
                       .projectName(yarnResult.getProjectName())
                       .projectVersion(yarnResult.getProjectVersionName())
                       .success(yarnResult.getCodeLocation())
                       .build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
