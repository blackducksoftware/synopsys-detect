/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.search;

import java.io.File;
import java.util.List;

import com.synopsys.integration.detect.util.filter.DetectFilter;

public class SearchOptions {
    public File searchPath;
    public final List<String> excludedDirectories;
    public final List<String> excludedDirectoryPatterns;
    public final boolean forceNestedSearch;
    public final int maxDepth;
    public final DetectFilter detectorFilter;

    public SearchOptions(File searchPath, List<String> excludedDirectories, List<String> excludedDirectoryPatterns, boolean forceNestedSearch, int maxDepth, DetectFilter detectorFilter) {
        this.searchPath = searchPath;
        this.excludedDirectories = excludedDirectories;
        this.excludedDirectoryPatterns = excludedDirectoryPatterns;
        this.forceNestedSearch = forceNestedSearch;
        this.maxDepth = maxDepth;
        this.detectorFilter = detectorFilter;
    }
}
