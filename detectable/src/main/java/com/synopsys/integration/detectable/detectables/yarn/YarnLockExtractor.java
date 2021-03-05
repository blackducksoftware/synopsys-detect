/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
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

    public Extraction extract(File projectDir, File yarnLockFile, File rootPackageJsonFile) {
        try {
            List<String> yarnLockLines = FileUtils.readLines(yarnLockFile, StandardCharsets.UTF_8);
            YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockLines);
            PackageJson rootPackageJson = packageJsonFiles.read(rootPackageJsonFile);
            // TODO THIS IS WRONG/CHANGING:
            // Yarn 1 projects: yarn.lock does not contain an entry for the project, so we have to guess at deps based on package.json files
            boolean addAllWorkspaceDependenciesAsDirect = yarnLockOptions.includeAllWorkspaceDependencies();
            boolean getWorkspaceDependenciesFromWorkspacePackageJson = !yarnLock.isYarn2Project();
            Map<String, PackageJson> workspacePackageJsonsToProcess;
            if (addAllWorkspaceDependenciesAsDirect || getWorkspaceDependenciesFromWorkspacePackageJson) {
                workspacePackageJsonsToProcess = getWorkspacePackageJsons(projectDir, rootPackageJsonFile);
            } else {
                workspacePackageJsonsToProcess = new HashMap<>();
            }
            YarnResult yarnResult = yarnPackager.generateYarnResult(rootPackageJson, workspacePackageJsonsToProcess, yarnLock, yarnLockFile.getAbsolutePath(), new ArrayList<>(),
                yarnLockOptions.useProductionOnly(), addAllWorkspaceDependenciesAsDirect, getWorkspaceDependenciesFromWorkspacePackageJson);

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

    @NotNull
    private Map<String, PackageJson> getWorkspacePackageJsons(File projectDir, File packageJsonFile) throws IOException {
        List<String> workspaceDirPatterns = packageJsonFiles.extractWorkspaceDirPatterns(packageJsonFile);
        return packageJsonFiles.readWorkspaceFiles(projectDir, workspaceDirPatterns);
    }
}
