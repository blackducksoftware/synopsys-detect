/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.util

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectConfiguration

@Component
class SourcePathSearcher {
    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    FileFinder fileFinder

    /**
     * Across all provided source paths, find the subset of source paths that
     * include the provided pattern. You would use the filenamePattern
     * 'pom.xml' to find all maven source paths.
     */
    List<String> findSourcePathsContainingFilenamePattern(String filenamePattern) {
        List<String> matchingSourcePaths = []
        for (String sourcePath : detectConfiguration.getSourcePaths()) {
            if (fileFinder.containsAllFiles(sourcePath, filenamePattern)) {
                matchingSourcePaths.add(sourcePath)
            }
        }

        matchingSourcePaths
    }

    /**
     * Across all provided source paths, find the subset of source paths that
     * include the provided pattern, within it and its sub-directories. You would use the filenamePattern
     * 'pom.xml' to find all maven source paths.
     */
    List<String> findSourcePathsContainingFilenamePatternWithDepth(String filenamePattern) {
        List<String> matchingSourcePaths = []
        for (String sourcePath : detectConfiguration.getSourcePaths()) {
            if (fileFinder.containsAllFilesWithDepth(sourcePath, detectConfiguration.getSearchDepth(), filenamePattern)) {
                matchingSourcePaths.add(sourcePath)
            }
        }

        matchingSourcePaths
    }
}
