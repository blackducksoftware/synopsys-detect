/**
 * detector
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
package com.synopsys.integration.detector.result;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FilesNotFoundDetectorResult extends FailedDetectorResult {
    private final String[] patterns;

    public FilesNotFoundDetectorResult(final String... patterns) {
        this.patterns = patterns;
    }

    @Override
    public String toDescription() {
        return "No files were found with any of the patterns: " + Arrays.asList(patterns).stream().collect(Collectors.joining(","));
    }
}
