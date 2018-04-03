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
package com.blackducksoftware.integration.hub.detect.bomtool.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.bomtool.NestedBomTool;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class BomToolTreeSearcher {
    private final Boolean bomToolForceSearch;

    private List<NestedBomToolResult> results = new ArrayList<>();

    public BomToolTreeSearcher(final Boolean bomToolForceSearch) {
        this.bomToolForceSearch = bomToolForceSearch;
    }

    public List<NestedBomToolResult> getResults() {
        return results;
    }

    public void startSearching(final Set<NestedBomTool> nestedBomTools, final File initialDirectory, int maximumDepth) throws BomToolException {
        List<File> subDirectories = getSubDirectories(initialDirectory);

        searchDirectories(results, nestedBomTools, subDirectories, 1, maximumDepth);
    }

    private void searchDirectories(List<NestedBomToolResult> results, final Set<NestedBomTool> nestedBomTools, List<File> directoriesToSearch, int depth, int maximumDepth) throws BomToolException {
        if (depth > maximumDepth) {
            return;
        }

        if (null == directoriesToSearch || directoriesToSearch.size() == 0) {
            return;
        }

        for (File directory : directoriesToSearch) {
            Set<NestedBomTool> remainingNestedBomTools = new HashSet<>();
            if (bomToolForceSearch) {
                remainingNestedBomTools.addAll(nestedBomTools);
            }
            for (NestedBomTool nestedBomTool : nestedBomTools) {
                BomToolSearcher bomToolSearcher = nestedBomTool.getBomToolSearcher();
                //                if (nestedBomTool.getDirectoriesToExclude().contains(directory)) {
                //                    continue;
                //                }
                BomToolSearchResult searchResult = bomToolSearcher.getBomToolSearchResult(directory);
                if (searchResult.isApplicable()) {
                    List<DetectCodeLocation> detectCodeLocations = nestedBomTool.extractDetectCodeLocations(searchResult);
                    NestedBomToolResult result = new NestedBomToolResult(nestedBomTool.getBomToolType(), directory, detectCodeLocations);
                    results.add(result);
                    if (nestedBomTool.canSearchWithinApplicableDirectory()) {
                        remainingNestedBomTools.add(nestedBomTool);
                    }
                } else {
                    remainingNestedBomTools.add(nestedBomTool);
                }
            }
            if (!remainingNestedBomTools.isEmpty()) {
                searchDirectories(results, remainingNestedBomTools, getSubDirectories(directory), depth + 1, maximumDepth);
            }
        }
    }

    private List<File> getSubDirectories(File directory) throws BomToolException {
        try {
            return Files.list(directory.toPath())
                           .filter(path -> Files.isDirectory(path))
                           .map(path -> path.toFile())
                           .collect(Collectors.toList());
        } catch (IOException e) {
            throw new BomToolException(e.getMessage(), e);
        }
    }
}
