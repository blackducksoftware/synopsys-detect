/**
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
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonCurrent;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonPreV1_5_0;
import com.synopsys.integration.detectable.extraction.Extraction;

public class YarnLockExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YarnPackager yarnPackager;

    public YarnLockExtractor(YarnPackager yarnPackager) {
        this.yarnPackager = yarnPackager;
    }

    public Extraction extract(File sourceDir, File yarnLockFile, File rootPackageJsonFile) {
        try {
            String rootPackageJsonText = FileUtils.readFileToString(rootPackageJsonFile, StandardCharsets.UTF_8);
            ////////////////////////////////
            ////List<PackageJson> workspacePackageJsons = new LinkedList<>();
            Map<String, PackageJson> workspacePackageJsons = new HashMap<>();
            // TODO gson work used to only happen in yarnPackager
            // Doesn't seem to belong here; maybe a new class for all the gson work?
            // It's the same code here and in yarnPackager
            Gson gson = new Gson();
            GsonBuilder builder = new GsonBuilder();
            Map<String, Object> packageJsonMap = builder.create().fromJson(rootPackageJsonText, Map.class);
            Object workspacesObject = packageJsonMap.get("workspaces");
            List<String> workspaceSubdirPatterns = new LinkedList<>();
            if (workspacesObject != null) {
                System.out.printf("workspacesObject type: %s\n", workspacesObject.getClass().getName());
                if (workspacesObject instanceof Map) {
                    System.out.printf("workspacesObject is a Map\n");
                    PackageJsonCurrent rootPackageJsonCurrent = gson.fromJson(rootPackageJsonText, PackageJsonCurrent.class);
                    // TODO pull workspaces out to a neutral format, like List<String>
                    workspaceSubdirPatterns.addAll(rootPackageJsonCurrent.workspaces.workspaceSubdirPatterns);
                } else if (workspacesObject instanceof List) {
                    System.out.printf("workspacesObject is a List\n");
                    PackageJsonPreV1_5_0 rootPackageJsonPreV1_5_0 = gson.fromJson(rootPackageJsonText, PackageJsonPreV1_5_0.class);
                    // TODO pull workspaces out to a neutral format
                    workspaceSubdirPatterns.addAll(rootPackageJsonPreV1_5_0.workspaceSubdirPatterns);
                } else {
                    System.out.printf("workspacesObject is something I don't understand\n");
                }
            }

            // TODO factor this out:
            for (String workspaceSubdirPattern : workspaceSubdirPatterns) {
                int prevWorkspaceCount = workspacePackageJsons.size();
                logger.info("workspaceSubdirPattern: {}", workspaceSubdirPattern);
                String globString = String.format("glob:%s/%s/package.json", sourceDir.getAbsolutePath(), workspaceSubdirPattern);
                logger.info("workspace subdir globString: {}", globString);
                PathMatcher matcher = FileSystems.getDefault().getPathMatcher(globString);
                Files.walkFileTree(sourceDir.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (matcher.matches(file)) {
                            logger.info("\tFound a match: {}", file.toString());
                            String workspacePackageJsonText = FileUtils.readFileToString(file.toFile(), StandardCharsets.UTF_8);
                            PackageJson workspacePackageJson = gson.fromJson(workspacePackageJsonText, PackageJson.class);
                            workspacePackageJsons.put(workspacePackageJson.name, workspacePackageJson);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });
                if (workspacePackageJsons.size() == prevWorkspaceCount) {
                    logger.info("Workspace search: No matches found for {}", workspaceSubdirPattern);
                }
            }

            ////////////////////////////////
            // TODO any way to avoid re-reading the json here?
            PackageJson rootPackageJson = gson.fromJson(rootPackageJsonText, PackageJson.class);
            List<String> yarnLockLines = FileUtils.readLines(yarnLockFile, StandardCharsets.UTF_8);
            YarnResult yarnResult = yarnPackager.generateYarnResult(rootPackageJson, workspacePackageJsons,
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
