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
package com.blackducksoftware.integration.hub.detect.bomtool;

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

import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.util.ResourceUtil;

public class BomToolFinder {
    private final Logger logger = LoggerFactory.getLogger(BomToolFinder.class);

    private final List<String> excludedDirectoriesBomToolSearch;
    private final Boolean bomToolSearchExclusionDefaults;
    private final Boolean bomToolContinueSearch;
    private final int maximumDepth;

    // Think three stages:
    // 1. Search for applicable (nuget applies).
    //      Complications:
    //          Docker: not the most straightforward applies - uses properties not necessarily files to indicate
    //          GO uses other GO applicables to decide
    //          NPM uses YARN applicables to decide
    // 2. Check the environment (nuget exists, install inspector) just once (instead of every applicable).
    //      Complications:
    //          Some executable inside the source directory, gradle?
    //          Right now some inspectors resolve during configuration init if the BomTool applies.
    //          We will no longer know during init if the bom tool applies.
    // 3. Execute applicable (nuget extracts)
    //      This will have multiple stages. Might be complex due to cleanup.
    //      I'd also like to let bom tools nominate project names
    // 4. Transform results.

    public BomToolFinder(final List<String> excludedDirectoriesBomToolSearch, final Boolean bomToolSearchExclusionDefaults, final Boolean bomToolContinueSearch, final int maximumDepth) {
        this.excludedDirectoriesBomToolSearch = excludedDirectoriesBomToolSearch;
        this.bomToolSearchExclusionDefaults = bomToolSearchExclusionDefaults;
        this.bomToolContinueSearch = bomToolContinueSearch;
        this.maximumDepth = maximumDepth;
    }

    public List<BomToolApplicableResult> findApplicableBomTools(final Set<BomTool> bomTools, final File initialDirectory) throws BomToolException, DetectUserFriendlyException {
        final List<String> excludedDirectories = determineDirectoriesToExclude(excludedDirectoriesBomToolSearch, bomToolSearchExclusionDefaults);
        final List<File> subDirectories = getSubDirectories(initialDirectory, excludedDirectories);
        return findApplicableBomTools(excludedDirectories, bomTools, subDirectories, 1, maximumDepth);
    }

    private List<BomToolApplicableResult> findApplicableBomTools(final List<String> directoriesToExclude, final Set<BomTool> bomTools, final List<File> directoriesToSearch, final int depth, final int maximumDepth)
            throws BomToolException, DetectUserFriendlyException {

        final List<BomToolApplicableResult> results = new ArrayList<>();

        if (depth > maximumDepth) {
            return results;
        }

        if (null == directoriesToSearch || directoriesToSearch.size() == 0) {
            return results;
        }
        for (final File directory : directoriesToSearch) {
            final Set<BomTool> remainingBomTools = new HashSet<>(bomTools);
            for (final BomTool bomTool : bomTools) {
                final BomToolApplicableResult searchResult = bomTool.isBomToolApplicable(directory);
                if (searchResult != null) {
                    results.add(searchResult);
                    if (shouldStopSearchingIfApplicable(bomTool)) {
                        remainingBomTools.remove(bomTool);
                    }
                }
            }
            if (!remainingBomTools.isEmpty()) {
                final List<BomToolApplicableResult> recursiveResults = findApplicableBomTools(directoriesToExclude, remainingBomTools, getSubDirectories(directory, directoriesToExclude), depth + 1, maximumDepth);
                results.addAll(recursiveResults);
            }
        }

        return results;
    }

    private boolean shouldStopSearchingIfApplicable(final BomTool bomTool) {
        if (bomToolContinueSearch) {
            return false;
        }
        if (bomTool.getSearchOptions().canSearchWithinApplicableDirectoryies()) {
            return false;
        }
        return true;
    }

    private List<File> getSubDirectories(final File directory, final List<String> excludedDirectories) throws DetectUserFriendlyException {
        try {
            // only include directories that do not match the excluded directories
            final Predicate<File> excludeDirectoriesPredicate = file -> {
                boolean matchesExcludedDirectory = false;
                for (final String excludedDirectory : excludedDirectories) {
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
        } catch (final IOException e) {
            throw new DetectUserFriendlyException(String.format("Could not get the subdirectories for %s. %s", directory.getAbsolutePath(), e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    private List<String> determineDirectoriesToExclude(final List<String> excludedDirectoriesBomToolSearch, final Boolean bomToolSearchExclusionDefaults) throws DetectUserFriendlyException {
        final List<String> directoriesToExclude = new ArrayList<>(excludedDirectoriesBomToolSearch);
        try {
            if (bomToolSearchExclusionDefaults) {
                final String fileContent = ResourceUtil.getResourceAsString(BomToolFinder.class, "/excludedDirectoriesBomToolSearch.txt", StandardCharsets.UTF_8);
                directoriesToExclude.addAll(Arrays.asList(fileContent.split("\n")));
            }
        } catch (final IOException e) {
            throw new DetectUserFriendlyException(String.format("Could not determine the directories to exclude from the bom tool search. %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
        logger.debug("Excluding these directories from the bom tool search");
        directoriesToExclude.forEach(logger::debug);
        return directoriesToExclude;
    }

}
