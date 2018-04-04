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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

import com.blackducksoftware.integration.hub.detect.bomtool.NestedBomTool;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.util.ResourceUtil;

public class BomToolTreeSearcher {
    private final IntLogger logger;
    private final Boolean bomToolForceSearch;

    private List<NestedBomToolResult> results = new ArrayList<>();

    public BomToolTreeSearcher(final IntLogger logger, final Boolean bomToolForceSearch) {
        this.logger = logger;
        this.bomToolForceSearch = bomToolForceSearch;
    }

    public List<NestedBomToolResult> getResults() {
        return results;
    }

    public void startSearching(final String excludedDirectoriesBomToolSearchFilePath, final Set<NestedBomTool> nestedBomTools, final File initialDirectory, final int maximumDepth) throws BomToolException, DetectUserFriendlyException {
        List<String> excludedDirectories = determineDirectoriesToExclude(excludedDirectoriesBomToolSearchFilePath);
        List<File> subDirectories = getSubDirectories(initialDirectory, excludedDirectories);
        searchDirectories(results, excludedDirectories, nestedBomTools, subDirectories, 1, maximumDepth);
    }

    private void searchDirectories(final List<NestedBomToolResult> results, final List<String> directoriesToExclude, final Set<NestedBomTool> nestedBomTools, final List<File> directoriesToSearch, final int depth, final int maximumDepth)
            throws BomToolException, DetectUserFriendlyException {
        if (depth > maximumDepth) {
            return;
        }

        if (null == directoriesToSearch || directoriesToSearch.size() == 0) {
            return;
        }

        for (File directory : directoriesToSearch) {
            Set<NestedBomTool> remainingNestedBomTools;
            if (bomToolForceSearch) {
                remainingNestedBomTools = nestedBomTools;
            } else {
                remainingNestedBomTools = new HashSet<>();
            }
            for (NestedBomTool nestedBomTool : nestedBomTools) {
                BomToolSearcher bomToolSearcher = nestedBomTool.getBomToolSearcher();
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
                searchDirectories(results, directoriesToExclude, remainingNestedBomTools, getSubDirectories(directory, directoriesToExclude), depth + 1, maximumDepth);
            }
        }
    }

    private List<File> getSubDirectories(final File directory, final List<String> excludedDirectories) throws DetectUserFriendlyException {
        try {
            Predicate<File> excludeDirectoriesPredicate;
            if (bomToolForceSearch) {
                // include all directories
                excludeDirectoriesPredicate = file -> true;
            } else {
                // only include directories that do not match the excluded directories
                excludeDirectoriesPredicate = file -> {
                    boolean matches = false;
                    for (String excludedDirectory : excludedDirectories) {
                        String name = FilenameUtils.removeExtension(file.getName());
                        if (FilenameUtils.wildcardMatchOnSystem(name, excludedDirectory)) {
                            matches = true;
                            break;
                        }
                    }
                    return !matches;
                };
            }

            return Files.list(directory.toPath())
                           .map(path -> path.toFile())
                           .filter(file -> file.isDirectory())
                           .filter(excludeDirectoriesPredicate)
                           .collect(Collectors.toList());
        } catch (IOException e) {
            throw new DetectUserFriendlyException(String.format("Could not get the subdirectories for %s. %s", directory.getAbsolutePath(), e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    private List<String> determineDirectoriesToExclude(final String excludedDirectoriesBomToolSearchFilePath) throws DetectUserFriendlyException {
        try {
            File excludedDirectoriesBomToolSearchFile = new File(excludedDirectoriesBomToolSearchFilePath);
            if (excludedDirectoriesBomToolSearchFile.exists()) {
                return Files.readAllLines(excludedDirectoriesBomToolSearchFile.toPath(), StandardCharsets.UTF_8);
            } else {
                String fileContent = ResourceUtil.getResourceAsString(BomToolTreeSearcher.class, "/excludedDirectoriesBomToolSearch.txt", StandardCharsets.UTF_8);
                List<String> directoriesToExclude = Arrays.asList(fileContent.split("\n"));
                logger.info("Excluding these default directories from the search");
                directoriesToExclude.forEach(name -> logger.info(name));
                return directoriesToExclude;
            }
        } catch (IOException e) {
            throw new DetectUserFriendlyException(String.format("Could not determine the directories to exclude from the bom tool search. %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}
