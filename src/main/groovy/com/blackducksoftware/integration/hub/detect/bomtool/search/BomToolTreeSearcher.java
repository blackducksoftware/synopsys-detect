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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.NestedBomTool;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.util.ResourceUtil;

public class BomToolTreeSearcher {
    private final Logger logger = LoggerFactory.getLogger(BomToolTreeSearcher.class);

    private final List<String> excludedDirectoriesBomToolSearch;
    private final Boolean bomToolSearchExclusionDefaults;
    private final Boolean bomToolContinueSearch;
    private final int maximumDepth;

    private List<NestedBomToolResult> results = new ArrayList<>();

    public BomToolTreeSearcher(final List<String> excludedDirectoriesBomToolSearch, Boolean bomToolSearchExclusionDefaults, final Boolean bomToolContinueSearch, final int maximumDepth) {
        this.excludedDirectoriesBomToolSearch = excludedDirectoriesBomToolSearch;
        this.bomToolSearchExclusionDefaults = bomToolSearchExclusionDefaults;
        this.bomToolContinueSearch = bomToolContinueSearch;
        this.maximumDepth = maximumDepth;
    }

    public List<NestedBomToolResult> getResults() {
        return results;
    }

    public void startSearching(final Set<NestedBomTool> nestedBomTools, final File initialDirectory) throws BomToolException, DetectUserFriendlyException {
        List<String> excludedDirectories = determineDirectoriesToExclude(excludedDirectoriesBomToolSearch, bomToolSearchExclusionDefaults);
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
            Set<NestedBomTool> remainingNestedBomTools = new HashSet<>(nestedBomTools);
            for (NestedBomTool nestedBomTool : nestedBomTools) {
                BomToolSearcher bomToolSearcher = nestedBomTool.getBomToolSearcher();
                BomToolSearchResult searchResult = bomToolSearcher.getBomToolSearchResult(directory);
                if (searchResult.isApplicable()) {
                    List<DetectCodeLocation> detectCodeLocations = nestedBomTool.extractDetectCodeLocations(searchResult);
                    NestedBomToolResult result = new NestedBomToolResult(nestedBomTool.getBomToolType(), directory, detectCodeLocations);
                    results.add(result);
                    if (shouldStopSearchingIfApplicable(nestedBomTool)) {
                        remainingNestedBomTools.remove(nestedBomTool);
                    }
                }
            }
            if (!remainingNestedBomTools.isEmpty()) {
                searchDirectories(results, directoriesToExclude, remainingNestedBomTools, getSubDirectories(directory, directoriesToExclude), depth + 1, maximumDepth);
            }
        }
    }

    private boolean shouldStopSearchingIfApplicable(final NestedBomTool nestedBomTool) {
        if (bomToolContinueSearch) {
            return false;
        }
        if (nestedBomTool.canSearchWithinApplicableDirectory()) {
            return false;
        }
        return true;
    }

    private List<File> getSubDirectories(final File directory, final List<String> excludedDirectories) throws DetectUserFriendlyException {
        try {
            Predicate<File> excludeDirectoriesPredicate;
            // only include directories that do not match the excluded directories
            excludeDirectoriesPredicate = file -> {
                boolean matchesExcludedDirectory = false;
                for (String excludedDirectory : excludedDirectories) {
                    if (FilenameUtils.wildcardMatchOnSystem(file.getName(), excludedDirectory)) {
                        matchesExcludedDirectory = true;
                        break;
                    }
                }
                return !matchesExcludedDirectory;
            };

            return Files.list(directory.toPath())
                           .map(path -> path.toFile())
                           .filter(file -> file.isDirectory())
                           .filter(excludeDirectoriesPredicate)
                           .collect(Collectors.toList());
        } catch (IOException e) {
            throw new DetectUserFriendlyException(String.format("Could not get the subdirectories for %s. %s", directory.getAbsolutePath(), e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    private List<String> determineDirectoriesToExclude(final List<String> excludedDirectoriesBomToolSearch, Boolean bomToolSearchExclusionDefaults) throws DetectUserFriendlyException {
        List<String> directoriesToExclude = new ArrayList<>(excludedDirectoriesBomToolSearch);
        try {
            if (bomToolSearchExclusionDefaults) {
                String fileContent = ResourceUtil.getResourceAsString(BomToolTreeSearcher.class, "/excludedDirectoriesBomToolSearch.txt", StandardCharsets.UTF_8);
                directoriesToExclude.addAll(Arrays.asList(fileContent.split("\n")));
            }
        } catch (IOException e) {
            throw new DetectUserFriendlyException(String.format("Could not determine the directories to exclude from the bom tool search. %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
        logger.debug("Excluding these directories from the bom tool search");
        directoriesToExclude.forEach(logger::debug);
        return directoriesToExclude;
    }

}
