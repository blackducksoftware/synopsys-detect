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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.bomtool.NestedBomTool;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class BomToolTreeSearcher {
    private List<NestedBomToolResult> results = new ArrayList<>();

    public List<NestedBomToolResult> getResults() {
        return results;
    }

    public void startSearching(final List<NestedBomTool> nestedBomTools, final File initialDirectory, int maximumDepth) throws BomToolException {
        File[] subDirectories = getSubDirectories(initialDirectory);

        for (NestedBomTool nestedBomTool : nestedBomTools) {
            searchDirectories(results, nestedBomTool, subDirectories, 1, maximumDepth);
        }
    }

    private <T extends BomToolSearchResult> void searchDirectories(List<NestedBomToolResult> results, NestedBomTool<T> nestedBomTool, File[] directoriesToSearch, int depth, int maximumDepth) throws BomToolException {
        if (depth > maximumDepth) {
            return;
        }

        if (directoriesToSearch == null || directoriesToSearch.length == 0) {
            return;
        }

        BomToolSearcher<T> bomToolSearcher = nestedBomTool.getBomToolSearcher();
        for (File directory : directoriesToSearch) {
            T searchResult = bomToolSearcher.getBomToolSearchResult(directory);
            if (searchResult.isApplicable()) {
                List<DetectCodeLocation> detectCodeLocations = nestedBomTool.extractDetectCodeLocations(searchResult);
                NestedBomToolResult result = new NestedBomToolResult(nestedBomTool.getBomToolType(), directory, detectCodeLocations);
                results.add(result);
            }

            searchDirectories(results, nestedBomTool, getSubDirectories(directory), depth + 1, maximumDepth);
        }
    }

    private File[] getSubDirectories(File directory) {
        return directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                return file.isDirectory();
            }
        });
    }
}
