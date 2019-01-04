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
package com.blackducksoftware.integration.hub.detect.detector.gradle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class GradleReportLine {
    private final Logger logger = LoggerFactory.getLogger(GradleReportLine.class);

    private static final String[] DEPENDENCY_INDICATORS = new String[] { "+---", "\\---" };
    private static final String[] PROJECT_INDICATORS = new String[] { "+--- project ", "\\--- project " };
    private static final String COMPONENT_PREFIX = "--- ";
    private static final String SEEN_ELSEWHERE_SUFFIX = " (*)";
    private static final String WINNING_INDICATOR = " -> ";

    private final String originalLine;

    public GradleReportLine(final String line) {
        this.originalLine = line;
    }

    public boolean isComponentLine() {
        return originalLine.contains(COMPONENT_PREFIX);
    }

    public Dependency createDependencyNode(final ExternalIdFactory externalIdFactory) {
        if (!originalLine.contains(COMPONENT_PREFIX)) {
            return null;
        }

        String cleanedOutput = StringUtils.trimToEmpty(originalLine);
        cleanedOutput = cleanedOutput.substring(cleanedOutput.indexOf(COMPONENT_PREFIX) + COMPONENT_PREFIX.length());

        if (cleanedOutput.endsWith(SEEN_ELSEWHERE_SUFFIX)) {
            final int lastSeenElsewhereIndex = cleanedOutput.lastIndexOf(SEEN_ELSEWHERE_SUFFIX);
            cleanedOutput = cleanedOutput.substring(0, lastSeenElsewhereIndex);
        }

        // we might need to modify the returned list, so it needs to be an actual ArrayList
        List<String> gavPieces = new ArrayList<>(Arrays.asList(cleanedOutput.split(":")));
        if (cleanedOutput.contains(WINNING_INDICATOR)) {
            // WINNING_INDICATOR can point to an entire GAV not just a version
            final String winningSection = cleanedOutput.substring(cleanedOutput.indexOf(WINNING_INDICATOR) + WINNING_INDICATOR.length());
            if (winningSection.contains(":")) {
                gavPieces = Arrays.asList(winningSection.split(":"));
            } else {
                // the WINNING_INDICATOR is not always preceded by a : so if isn't, we need to clean up from the original split
                if (gavPieces.get(1).contains(WINNING_INDICATOR)) {
                    final String withoutWinningIndicator = gavPieces.get(1).substring(0, gavPieces.get(1).indexOf(WINNING_INDICATOR));
                    gavPieces.set(1, withoutWinningIndicator);
                    // since there was no : we don't have a gav piece for version yet
                    gavPieces.add("");
                }
                gavPieces.set(2, winningSection);
            }
        }

        if (gavPieces.size() != 3) {
            logger.error(String.format("The line can not be reasonably split in to the neccessary parts: %s", originalLine));
            return null;
        }

        final String group = gavPieces.get(0);
        final String artifact = gavPieces.get(1);
        final String version = gavPieces.get(2);
        final Dependency dependency = new Dependency(artifact, version, externalIdFactory.createMavenExternalId(group, artifact, version));
        return dependency;
    }

    public int getTreeLevel() {
        if (isRootLevel()) {
            return 0;
        }

        String modifiedLine = removeDependencyIndicators();

        if (!modifiedLine.startsWith("|")) {
            modifiedLine = "|" + modifiedLine;
        }
        modifiedLine = modifiedLine.replace("     ", "    |");
        modifiedLine = modifiedLine.replace("||", "|");
        if (modifiedLine.endsWith("|")) {
            modifiedLine = modifiedLine.substring(0, modifiedLine.length() - 5);
        }
        final int matches = StringUtils.countMatches(modifiedLine, "|");

        return matches;
    }

    public boolean isRootLevel() {
        return isRootLevelProject() || isRootLevelDependency();
    }

    public boolean isRootLevelDependency() {
        return startsWithAny(originalLine, DEPENDENCY_INDICATORS);
    }

    public boolean isRootLevelProject() {
        return startsWithAny(originalLine, PROJECT_INDICATORS);
    }

    private boolean startsWithAny(final String thing, final String[] targets) {
        for (final String target : targets) {
            if (thing.startsWith(target)) {
                return true;
            }
        }
        return false;
    }

    private String removeDependencyIndicators() {
        int indexToCut = originalLine.length();
        for (final String target : DEPENDENCY_INDICATORS) {
            if (originalLine.contains(target)) {
                indexToCut = originalLine.indexOf(target);
            }
        }

        return originalLine.substring(0, indexToCut);
    }

}
