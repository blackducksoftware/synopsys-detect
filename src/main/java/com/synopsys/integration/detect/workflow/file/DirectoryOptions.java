/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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

import java.nio.file.Path;

public class DirectoryOptions {
    private final String sourcePath;
    private final String outputPath;
    private final String bdioOutputPath;
    private final String scanOutputPath;
    private final String toolsOutputPath;

    public DirectoryOptions(final String sourcePath, final String outputPath, final String bdioOutputPath, final String scanOutputPath, final String toolsOutputPath) {
        this.sourcePath = sourcePath;
        this.outputPath = outputPath;
        this.bdioOutputPath = bdioOutputPath;
        this.scanOutputPath = scanOutputPath;
        this.toolsOutputPath = toolsOutputPath;
    }

    // TODO: Switch data types from Strings to Paths
    public DirectoryOptions(final Path sourcePath, final Path outputPath, final Path bdioOutputPath, final Path scanOutputPath, final Path toolsOutputPath) {
        this.sourcePath = sourcePath.toString();
        this.outputPath = outputPath.toString();
        this.bdioOutputPath = bdioOutputPath.toString();
        this.scanOutputPath = scanOutputPath.toString();
        this.toolsOutputPath = toolsOutputPath.toString();
    }

    public String getSourcePathOverride() {
        return sourcePath;
    }

    public String getOutputPathOverride() {
        return outputPath;
    }

    public String getBdioOutputPathOverride() {
        return bdioOutputPath;
    }

    public String getScanOutputPathOverride() {
        return scanOutputPath;
    }

    public String getToolsOutputPath() {
        return toolsOutputPath;
    }
}
