/**
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
package com.blackducksoftware.integration.hub.detect.bomtool.gradle;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class GradleConfigurationLine {
    private final Logger logger = LoggerFactory.getLogger(GradleConfigurationLine.class);

    public static final String[] DEPENDENCY_INDICATORS = new String[] { "+---", "\\---" };
    public static final String[] PROJECT_INDICATORS = new String[] { "+--- project :", "\\--- project :" };
    public static final String COMPONENT_PREFIX = "--- ";
    public static final String SEEN_ELSEWHERE_SUFFIX = " (*)";
    public static final String WINNING_INDICATOR = " -> ";

    private final String originalLine;

    public GradleConfigurationLine(final String line) {
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

        String[] gav = cleanedOutput.split(":");
        if (cleanedOutput.contains(WINNING_INDICATOR)) {
            // WINNING_INDICATOR can point to an entire GAV not just a version
            final String winningSection = cleanedOutput.substring(cleanedOutput.indexOf(WINNING_INDICATOR) + WINNING_INDICATOR.length());
            if (winningSection.contains(":")) {
                gav = winningSection.split(":");
            } else {
                gav[2] = winningSection;
            }
        }

        if (gav.length != 3) {
            logger.error(String.format("The line can not be reasonably split in to the neccessary parts: %s", originalLine));
            return null;
        }

        final String group = gav[0];
        final String artifact = gav[1];
        final String version = gav[2];
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
