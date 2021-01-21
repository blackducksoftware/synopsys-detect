/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
import java.util.List;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.extraction.Extraction;

public class YarnLockExtractor {
    private final YarnPackager yarnPackager;

    public YarnLockExtractor(YarnPackager yarnPackager) {
        this.yarnPackager = yarnPackager;
    }

    public Extraction extract(File sourceDir, File yarnLockFile, File rootPackageJsonFile) {
        try {
            String rootPackageJsonText = FileUtils.readFileToString(rootPackageJsonFile, StandardCharsets.UTF_8);
            ////////////////////////////////
            List<PackageJson> workspacePackageJsons = new TreeList<>();
            // TODO gson work used to only happen in yarnPackager
            // Doesn't seem to belong here; maybe a new class for all the gson work?
            // It's the same code here and in yarnPackager
            Gson gson = new Gson();
            PackageJson rootPackageJson = gson.fromJson(rootPackageJsonText, PackageJson.class);
            // TODO factor this out:
            for (String workspaceSubDir : rootPackageJson.workspaceSubdirsPreV1_5_0) {
                System.out.printf("workspaceSubDir: %s\n", workspaceSubDir);
                String globString = String.format("glob:%s/%s/package.json", sourceDir.getAbsolutePath(), workspaceSubDir);
                System.out.printf("globString: %s\n", globString);
                PathMatcher matcher = FileSystems.getDefault().getPathMatcher(globString);
                Files.walkFileTree(sourceDir.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (matcher.matches(file)) {
                            System.out.printf("\t*** Found a match: %s\n", file.toString());
                            String workspacePackageJsonText = FileUtils.readFileToString(file.toFile(), StandardCharsets.UTF_8);
                            PackageJson workspacePackageJson = gson.fromJson(workspacePackageJsonText, PackageJson.class);
                            workspacePackageJsons.add(workspacePackageJson);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            ////////////////////////////////
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
