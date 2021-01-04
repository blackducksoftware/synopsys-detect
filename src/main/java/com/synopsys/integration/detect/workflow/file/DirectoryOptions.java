/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class DirectoryOptions {
    private final Path sourcePath;
    private final Path outputPath;
    private final Path bdioOutputPath;
    private final Path scanOutputPath;
    private final Path toolsOutputPath;

    public DirectoryOptions(Path sourcePath, Path outputPath, Path bdioOutputPath, Path scanOutputPath, Path toolsOutputPath) throws IOException {
        this.sourcePath = toRealPath(sourcePath);
        this.outputPath = toRealPath(outputPath);
        this.bdioOutputPath = toRealPath(bdioOutputPath);
        this.scanOutputPath = toRealPath(scanOutputPath);
        this.toolsOutputPath = toRealPath(toolsOutputPath);
    }

    public Optional<Path> getSourcePathOverride() {
        return Optional.ofNullable(sourcePath);
    }

    public Optional<Path> getOutputPathOverride() {
        return Optional.ofNullable(outputPath);
    }

    public Optional<Path> getBdioOutputPathOverride() {
        return Optional.ofNullable(bdioOutputPath);
    }

    public Optional<Path> getScanOutputPathOverride() {
        return Optional.ofNullable(scanOutputPath);
    }

    public Optional<Path> getToolsOutputPath() {
        return Optional.ofNullable(toolsOutputPath);
    }

    @Nullable
    private Path toRealPath(@Nullable Path rawPath) throws IOException {
        if (rawPath == null) {
            return null;
        }
        return rawPath.toAbsolutePath();
    }
}
