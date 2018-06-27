/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DependencyFileManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<String> parse(final Optional<File> depsMkFile) {
        if (!depsMkFile.isPresent()) {
            return new ArrayList<>(0);
        }
        List<String> dependencyFilePaths;
        try {
            final String depsDecl = FileUtils.readFileToString(depsMkFile.get(), StandardCharsets.UTF_8);
            final String[] depsDeclParts = depsDecl.split(": ");
            String depsListString = depsDeclParts[1];
            logger.trace(String.format("dependencies: %s", depsListString));

            depsListString = depsListString.replaceAll("\n", " ");
            logger.trace(String.format("dependencies, newlines removed: %s", depsListString));

            depsListString = depsListString.replaceAll("\\\\", " ");
            logger.trace(String.format("dependencies, backslashes removed: %s", depsListString));

            final String[] deps = depsListString.split("\\s+");
            for (final String includeFile : deps) {
                logger.trace(String.format("\t%s", includeFile));
            }
            dependencyFilePaths = Arrays.asList(deps);
        } catch (final IOException e) {
            logger.warn(String.format("Error getting dependency file paths from '%s': %s", depsMkFile.get().getAbsolutePath(), e.getMessage()));
            return new ArrayList<>(0);
        }
        return dependencyFilePaths;
    }

    public void remove(final Optional<File> depsMkFile) {
        depsMkFile.ifPresent(f -> f.delete());
    }
}
