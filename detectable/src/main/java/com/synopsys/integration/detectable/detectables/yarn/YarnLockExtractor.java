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
import com.synopsys.integration.detectable.detectables.yarn.packagejson.WorkspacePackageJson;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.WorkspacePackageJsons;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLock;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.util.ExcludedIncludedWildcardFilter;

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
            boolean getWorkspaceDependenciesFromWorkspacePackageJson = !yarnLock.isYarn2Project();
            PackageJson rootPackageJson = packageJsonFiles.read(rootPackageJsonFile);
            Map<String, WorkspacePackageJson> locatedWorkspacePackageJsons = collectPackageJsons(projectDir);
            Map<String, PackageJson> workspacePackageJsons = WorkspacePackageJsons.toPackageJsons(locatedWorkspacePackageJsons);

            ExcludedIncludedWildcardFilter workspacesFilter;
            if (yarnLockOptions.getExcludedWorkspaceNamePatterns().isEmpty() && yarnLockOptions.getIncludedWorkspaceNamePatterns().isEmpty()) {
                workspacesFilter = null; // Just follow dependencies
            } else {
                workspacesFilter = ExcludedIncludedWildcardFilter.fromCollections(yarnLockOptions.getExcludedWorkspaceNamePatterns(), yarnLockOptions.getIncludedWorkspaceNamePatterns());
            }
            YarnResult yarnResult = yarnPackager.generateYarnResult(rootPackageJson, workspacePackageJsons, yarnLock, yarnLockFile.getAbsolutePath(), new ArrayList<>(),
                yarnLockOptions.useProductionOnly(), getWorkspaceDependenciesFromWorkspacePackageJson, workspacesFilter);

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
    private Map<String, WorkspacePackageJson> collectPackageJsons(File dir) throws IOException {
        Map<String, WorkspacePackageJson> curLevelWorkspacePackageJsons = packageJsonFiles.readWorkspacePackageJsonFiles(dir);
        Map<String, WorkspacePackageJson> allWorkspacePackageJsons = new HashMap<>(curLevelWorkspacePackageJsons);
        for (WorkspacePackageJson workspacePackageJson : curLevelWorkspacePackageJsons.values()) {
            Map<String, WorkspacePackageJson> treeBranchWorkspacePackageJsons = packageJsonFiles.readWorkspacePackageJsonFiles(workspacePackageJson.getPackageJsonFile().getParentFile());
            allWorkspacePackageJsons.putAll(treeBranchWorkspacePackageJsons);
        }
        return allWorkspacePackageJsons;
    }
}
