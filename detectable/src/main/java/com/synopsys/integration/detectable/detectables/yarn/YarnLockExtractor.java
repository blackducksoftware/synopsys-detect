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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonFiles;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLock;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParser;
import com.synopsys.integration.detectable.extraction.Extraction;

public class YarnLockExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YarnLockParser yarnLockParser;
    private final YarnPackager yarnPackager;
    private final PackageJsonFiles packageJsonFiles;
    private final YarnLockOptions yarnLockOptions;

    public YarnLockExtractor(YarnLockParser yarnLockParser, YarnPackager yarnPackager, PackageJsonFiles packageJsonFiles, YarnLockOptions yarnLockOptions) {
        this.yarnLockParser = yarnLockParser;
        this.yarnPackager = yarnPackager;
        this.packageJsonFiles = packageJsonFiles;
        this.yarnLockOptions = yarnLockOptions;
    }

    public Extraction extract(File yarnLockFile, File packageJsonFile) {
        try {
            String packageJsonText = FileUtils.readFileToString(packageJsonFile, StandardCharsets.UTF_8);
            List<String> yarnLockLines = FileUtils.readLines(yarnLockFile, StandardCharsets.UTF_8);
            YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockLines);
            // Yarn 1 projects: yarn.lock does not contain an entry for the project, so we have to guess at deps based on package.json files
            boolean addAllWorkspaceDependenciesAsDirect = yarnLockOptions.includeAllWorkspaceDependencies() || !yarnLock.isYarn2Project();
            PackageJson rootPackageJson = packageJsonFiles.read(packageJsonFile);
            List<PackageJson> workspacePackageJsons = new LinkedList<>();
            YarnResult yarnResult = yarnPackager.generateYarnResult(rootPackageJson, workspacePackageJsons, yarnLock, yarnLockFile.getAbsolutePath(), new ArrayList<>(),
                yarnLockOptions.useProductionOnly());

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
