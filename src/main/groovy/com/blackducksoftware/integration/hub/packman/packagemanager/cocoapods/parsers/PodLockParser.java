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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.CocoapodsPackager;
import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.model.Pod;
import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.model.PodLock;

public class PodLockParser {
    private final Logger logger = LoggerFactory.getLogger(PodLockParser.class);

    final Pattern PODS_SECTION = Pattern.compile("PODS:\\s*");

    final Pattern DEPENDENCIES_SECTION = Pattern.compile("DEPENDENCIES:\\s*");

    final Pattern SPEC_CHECKSUMS_SECTION = Pattern.compile("SPEC CHECKSUMS:\\s*");

    final Pattern PODFILE_CHECKSUM_SECTION = Pattern.compile("PODFILE CHECKSUM:(.*)");

    final Pattern COCOAPODS_SECTION = Pattern.compile("COCOAPODS:\\s*(.*)");

    final Pattern EXTERNAL_SOURCES_SECTION = Pattern.compile("EXTERNAL SOURCES:\\s*");

    final Pattern CHECKOUT_OPTIONS_SECTION = Pattern.compile("CHECKOUT OPTIONS:\\s*");

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

    public PodLock parse(final BufferedReader bufferedReader) {
        PodLock podLock = new PodLock();

        String section = null;
        DependencyNode subsection = null;
        Pod currentPod = null;

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                final Matcher podsSectionMatcher = PODS_SECTION.matcher(line);
                final Matcher dependenciesSectionMatcher = DEPENDENCIES_SECTION.matcher(line);
                final Matcher specChecksumSectionMatcher = SPEC_CHECKSUMS_SECTION.matcher(line);
                final Matcher podfileChecksumSectionMatcher = PODFILE_CHECKSUM_SECTION.matcher(line);
                final Matcher cocoapodsSectionMatcher = COCOAPODS_SECTION.matcher(line);
                final Matcher externalSourcesSectionMatcher = EXTERNAL_SOURCES_SECTION.matcher(line);
                final Matcher checkoutSectionMatcher = CHECKOUT_OPTIONS_SECTION.matcher(line);

                if (StringUtils.isBlank(line)) {

                } else if (cocoapodsSectionMatcher.matches()) {
                    section = COCOAPODS_SECTION.pattern();
                    podLock.cococapodsVersion = line.split(":")[1].trim();
                } else if (podsSectionMatcher.matches()) {
                    section = PODS_SECTION.pattern();
                } else if (dependenciesSectionMatcher.matches()) {
                    section = DEPENDENCIES_SECTION.pattern();
                } else if (specChecksumSectionMatcher.matches()) {
                    section = SPEC_CHECKSUMS_SECTION.pattern();
                } else if (podfileChecksumSectionMatcher.matches()) {
                    section = PODFILE_CHECKSUM_SECTION.pattern();
                    podLock.podfileChecksum = podfileChecksumSectionMatcher.group(1).trim();
                } else if (externalSourcesSectionMatcher.matches()) {
                    section = EXTERNAL_SOURCES_SECTION.pattern();
                } else if (checkoutSectionMatcher.matches()) {
                    section = CHECKOUT_OPTIONS_SECTION.pattern();
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
                } else if (section == SPEC_CHECKSUMS_SECTION.pattern()) {
                    final Matcher checksumMatcher = SPEC_CHECKSUMS_SECTION.matcher(line);
                    if (checksumMatcher.matches()) {
                        final String podName = checksumMatcher.group(1);
                        final String checksum = checksumMatcher.group(2);
                        podLock.specChecsums.put(podName, checksum);
                    }
                } else if (section == EXTERNAL_SOURCES_SECTION.pattern() || section == CHECKOUT_OPTIONS_SECTION.pattern()) {
                    final Matcher nameMatcher = POD_START_REGEX.matcher(line);
                    final Matcher commitMatcher = COMMIT_REGEX.matcher(line);
                    final Matcher urlMatcher = GIT_REGEX.matcher(line);
                    final Matcher branchMatcher = BRANCH_REGEX.matcher(line);
                    final Matcher tagMatcher = TAG_REGEX.matcher(line);

                    if (nameMatcher.matches()) {
                        final String podName = nameMatcher.group(1);
                        final Pod pod = new Pod(podName);
                        currentPod = pod;
                        if (section == EXTERNAL_SOURCES_SECTION.pattern()) {
                            podLock.externalSources.put(podName, pod);
                        } else {
                            podLock.checkoutOptions.put(podName, pod);
                        }
                    } else if (commitMatcher.matches()) {
                        currentPod.gitCommit = commitMatcher.group(1);
                    } else if (urlMatcher.matches()) {
                        currentPod.gitUrl = urlMatcher.group(1);
                    } else if (branchMatcher.matches()) {
                        currentPod.gitBranch = branchMatcher.group(1);
                    } else if (tagMatcher.matches()) {
                        currentPod.gitTag = tagMatcher.group(1);
                    }
                } else if (section == SPEC_CHECKSUMS_SECTION.pattern()) {
                    final Matcher checksumMatcher = SPEC_CHECKSUMS_SECTION.matcher(line);
                    if (checksumMatcher.matches()) {
                        final String podName = checksumMatcher.group(1);
                        final String checksum = checksumMatcher.group(2);
                        podLock.specChecsums.put(podName, checksum);
                    }
                } else {
                    logger.debug("PodLockParser: Couldn't find if statement for >" + line + "\n");
                }
            }
        } catch (final IOException e) {
            logger.debug("IOException when parsing Podfile.lock:" + e.getMessage());
            podLock = null;
        }
        return podLock;
    }
}
