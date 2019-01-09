/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.workflow.file;

public class DirectoryOptions {
    private String sourcePath;
    private String outputPath;
    private String bdioOutputPath;
    private String scanOutputPath;

    public DirectoryOptions(final String sourcePath, final String outputPath, final String bdioOutputPath, final String scanOutputPath) {
        this.sourcePath = sourcePath;
        this.outputPath = outputPath;
        this.bdioOutputPath = bdioOutputPath;
        this.scanOutputPath = scanOutputPath;
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
}
