/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.CocoapodsPackager;
import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.model.PodLock;

public class PodLockParser {
    private final Logger logger = LoggerFactory.getLogger(PodLockParser.class);

    final Pattern PODS_SECTION = Pattern.compile("PODS:\\s*");

    final Pattern DEPENDENCIES_SECTION = Pattern.compile("DEPENDENCIES:\\s*");

    final Pattern COCOAPODS_SECTION = Pattern.compile("COCOAPODS:\\s*(.*)");

    final Pattern POD_REGEX = Pattern.compile("  - (.*) *\\((.*)\\)");

    final Pattern POD_WITH_SUB_REGEX = Pattern.compile("  - (.*) *\\((.*)\\):");

    final Pattern SUBPOD_REGEX = Pattern.compile("    - (.*) *\\((.*)\\)");

    final Pattern SUBPOD_REGEX2 = Pattern.compile("    - (.*)()");

    final Pattern DEPENDENCY_REGEX = Pattern.compile("  - ([^ ]*)(( \\(.*\\))*)");

    final Pattern POD_START_REGEX = Pattern.compile("  (.*):\\s*");

    final Pattern BRANCH_REGEX = Pattern.compile("    :branch:\\s*(.*)");

    final Pattern GIT_REGEX = Pattern.compile("    :git:\\s*(.*)");

    final Pattern TAG_REGEX = Pattern.compile("    :tag:\\s*(.*)");

    final Pattern COMMIT_REGEX = Pattern.compile("    :commit:\\s*(.*)");

    public PodLock parse(final String podlockText) {
        final PodLock podLock = new PodLock();

        String section = null;
        DependencyNode subsection = null;

        for (final String line : podlockText.split("\n")) {
            final Matcher podsSectionMatcher = PODS_SECTION.matcher(line);
            final Matcher dependenciesSectionMatcher = DEPENDENCIES_SECTION.matcher(line);
            final Matcher cocoapodsSectionMatcher = COCOAPODS_SECTION.matcher(line);

            if (StringUtils.isBlank(line)) {

            } else if (cocoapodsSectionMatcher.matches()) {
                section = COCOAPODS_SECTION.pattern();
                podLock.cococapodsVersion = line.split(":")[1].trim();
            } else if (podsSectionMatcher.matches()) {
                section = PODS_SECTION.pattern();
            } else if (dependenciesSectionMatcher.matches()) {
                section = DEPENDENCIES_SECTION.pattern();
            } else if (section == PODS_SECTION.pattern()) {
                final Matcher podMatcher = POD_REGEX.matcher(line);
                final Matcher podWithSubMatcher = POD_WITH_SUB_REGEX.matcher(line);
                final Matcher subpodMatcher = SUBPOD_REGEX.matcher(line);
                final Matcher subpodMatcher2 = SUBPOD_REGEX2.matcher(line);

                if (podWithSubMatcher.matches()) {
                    final DependencyNode pod = CocoapodsPackager.createPodNodeFromGroups(podWithSubMatcher, 1, 2);
                    if (pod != null) {
                        subsection = pod;
                        podLock.pods.add(pod);
                    }
                } else if (subsection != null && subpodMatcher.matches()) {
                    final DependencyNode subpod = CocoapodsPackager.createPodNodeFromGroups(subpodMatcher, 1, 2);
                    if (subpod != null) {
                        subsection.children.add(subpod);
                    }
                } else if (subsection != null && subpodMatcher2.matches()) {
                    final DependencyNode subpod = CocoapodsPackager.createPodNodeFromGroups(subpodMatcher2, 1, 2);
                    if (subpod != null) {
                        subsection.children.add(subpod);
                    }
                } else if (podMatcher.matches()) {
                    final DependencyNode pod = CocoapodsPackager.createPodNodeFromGroups(podMatcher, 1, 2);
                    if (pod != null) {
                        podLock.pods.add(pod);
                        subsection = null;
                    }
                }
            } else if (section == DEPENDENCIES_SECTION.pattern()) {
                final Matcher dependencyMatcher = DEPENDENCY_REGEX.matcher(line);
                final DependencyNode dependency = CocoapodsPackager.createPodNodeFromGroups(dependencyMatcher, 1, 2);
                if (dependency != null) {
                    podLock.dependencies.add(dependency);
                } else {
                    logger.debug("Couldn't find match with [" + dependencyMatcher.pattern().pattern() + "] >" + line);
                }
            } else {
                logger.debug("PodLockParser: Couldn't find if statement for >" + line + "\n");
            }
        }
        return podLock;
    }
}
