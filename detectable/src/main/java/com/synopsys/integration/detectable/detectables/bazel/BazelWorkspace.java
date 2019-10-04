/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.bazel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BazelWorkspace {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final File workspaceFile;

    public BazelWorkspace(final File workspaceFile) {
        this.workspaceFile = workspaceFile;
    }

    public WorkspaceRule getDependencyRule() {
        final List<String> workspaceFileLines;
        try {
            workspaceFileLines = readWorkspaceFileLines(workspaceFile);
        } catch (IOException e) {
            logger.debug(String.format("Unable to parse dependency rule from %s: %s", workspaceFile.getAbsolutePath(), e.getMessage()));
            return WorkspaceRule.UNKNOWN;
        }
        final WorkspaceRule dependencyRule = parseDependencyRuleFromWorkspaceFileLines(workspaceFileLines);
        return dependencyRule;
    }

    @NotNull
    private List<String> readWorkspaceFileLines(final File workspaceFile) throws IOException {
        final List<String> workspaceFileLines;
        // Assumes ascii or UTF-8, like other detectors
        workspaceFileLines = FileUtils.readLines(workspaceFile, StandardCharsets.UTF_8);
        return workspaceFileLines;
    }

    @Nullable
    public WorkspaceRule parseDependencyRuleFromWorkspaceFileLines(final List<String> workspaceFileLines) {
        for (final String workspaceFileLine : workspaceFileLines) {
            for (final WorkspaceRule workspaceRuleCandidate : WorkspaceRule.values()) {
                if (workspaceFileLine.matches(String.format("^\\s*%s\\s*\\(", workspaceRuleCandidate.getName()))) {
                    final WorkspaceRule parsedDependencyRule = workspaceRuleCandidate;
                    logger.debug(String.format("Found workspace dependency rule: %s", parsedDependencyRule.getName()));
                    return parsedDependencyRule;
                }
            }
        }
        logger.debug("Unable to derive dependency rule from WORKSPACE file");
        return WorkspaceRule.UNKNOWN;
    }
}
