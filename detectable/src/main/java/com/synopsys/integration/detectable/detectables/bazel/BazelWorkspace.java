/*
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
package com.synopsys.integration.detectable.detectables.bazel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BazelWorkspace {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final File workspaceFile;

    public BazelWorkspace(File workspaceFile) {
        this.workspaceFile = workspaceFile;
    }

    public Set<WorkspaceRule> getDependencyRuleTypes() {
        List<String> workspaceFileLines;
        try {
            // Assumes ascii or UTF-8, like other detectors
            workspaceFileLines = FileUtils.readLines(workspaceFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.debug(String.format("Unable to parse dependency rule from %s: %s", workspaceFile.getAbsolutePath(), e.getMessage()));
            return new HashSet<>(0);
        }

        return workspaceFileLines.stream()
                   .flatMap(this::parseDependencyRulesFromWorkspaceFileLine)
                   .collect(Collectors.toSet());
    }

    @SuppressWarnings("java:S3864") // Sonar deems peek useful for debugging.
    public Stream<WorkspaceRule> parseDependencyRulesFromWorkspaceFileLine(String workspaceFileLine) {
        return Arrays.stream(WorkspaceRule.values())
                   .filter(workspaceRule -> workspaceFileLine.matches(String.format("^\\s*%s\\s*\\(", workspaceRule.getName())))
                   .peek(workspaceRule -> logger.debug(String.format("Found workspace dependency rule: %s", workspaceRule.getName())));
    }
}
