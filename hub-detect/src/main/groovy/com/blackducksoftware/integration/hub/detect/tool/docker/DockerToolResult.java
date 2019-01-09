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
package com.blackducksoftware.integration.hub.detect.tool.docker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.util.NameVersion;

public class DockerToolResult {
    public enum DockerToolResultType {
        SUCCESS,
        FAILURE,
        SKIPPED
    }

    public Optional<NameVersion> dockerProjectNameVersion = Optional.empty();
    public List<DetectCodeLocation> dockerCodeLocations = new ArrayList<>();
    public Optional<File> dockerTar = Optional.empty();

    public DockerToolResultType resultType = DockerToolResultType.SUCCESS;
    public String errorMessage;

    public static DockerToolResult failure(String message) {
        DockerToolResult result = new DockerToolResult();
        result.resultType = DockerToolResultType.FAILURE;
        result.errorMessage = message;
        return result;
    }

    public static DockerToolResult skipped() {
        DockerToolResult result = new DockerToolResult();
        result.resultType = DockerToolResultType.SKIPPED;
        return result;
    }
}