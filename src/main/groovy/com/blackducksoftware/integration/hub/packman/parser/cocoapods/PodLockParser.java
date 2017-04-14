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
package com.blackducksoftware.integration.hub.packman.parser.cocoapods;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.parser.StreamParser;

public class PodLockParser extends StreamParser<PodLock> {

    final String PODS_SECTION = "PODS:";

    final String DEPENDENCIES_SECTION = "DEPENDENCIES:";

    final String SPEC_CHECKSUMS_SECTION = "SPEC CHECKSUMS:";

    final String PODFILE_CHECKSUM_SECTION = "PODFILE CHECKSUM:";

    final String COCOAPODS_SECTION = "COCOAPODS:";

    final Pattern POD_REGEX = Pattern.compile("  - (.*)\\((.*)\\)");

    final Pattern POD_WITH_SUB_REGEX = Pattern.compile("  - (.*)\\((.*)\\):");

    final Pattern SUBPOD_REGEX = Pattern.compile("    - (.*)\\((.*)\\)");

    final Pattern DEPENDENCY_REGEX = Pattern.compile("  *- *(.*)\\((.*)\\)");

    final Pattern SPEC_CHECKSUM_REGEX = Pattern.compile("  (.*):(.*)");

    @Override
    public PodLock parse(final BufferedReader bufferedReader) {
        PodLock podLock = new PodLock();

        String section = null;
        DependencyNode subsection = null;

        String line;
        int lineNumber = 0;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                lineNumber++;

                final Matcher podMatcher = POD_REGEX.matcher(line);
                final Matcher podWithSubMatcher = POD_WITH_SUB_REGEX.matcher(line);
                final Matcher subpodMatcher = SUBPOD_REGEX.matcher(line);
                final Matcher dependencyMatcher = DEPENDENCY_REGEX.matcher(line);
                final Matcher checksumMatcher = SPEC_CHECKSUM_REGEX.matcher(line);

                if (StringUtils.isBlank(line)) {

                } else if (line.contains(COCOAPODS_SECTION)) {
                    section = COCOAPODS_SECTION;
                    podLock.cococapodsVersion = line.split(":")[1].trim();
                } else if (line.contains(PODS_SECTION)) {
                    section = PODS_SECTION;
                } else if (line.contains(DEPENDENCIES_SECTION)) {
                    section = DEPENDENCIES_SECTION;
                } else if (line.contains(SPEC_CHECKSUMS_SECTION)) {
                    section = SPEC_CHECKSUMS_SECTION;
                } else if (line.contains(PODFILE_CHECKSUM_SECTION)) {
                    section = PODFILE_CHECKSUM_SECTION;
                    podLock.podfileChecksum = line.split(":")[1].trim();
                } else if (section == PODS_SECTION) {
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
                    } else if (podMatcher.matches()) {
                        final DependencyNode pod = CocoapodsPackager.createPodNodeFromGroups(podMatcher, 1, 2);
                        if (pod != null) {
                            podLock.pods.add(pod);
                            subsection = null;
                        }
                    }
                } else if (section == DEPENDENCIES_SECTION && dependencyMatcher.matches()) {
                    final DependencyNode dependency = CocoapodsPackager.createPodNodeFromGroups(dependencyMatcher, 1, 2);
                    if (dependency != null) {
                        podLock.dependencies.add(dependency);
                    } else {
                        System.out.println("Couldn't find match with [" + dependencyMatcher.pattern().pattern() + "] >" + line);
                    }
                } else if (section == SPEC_CHECKSUMS_SECTION) {
                    if (checksumMatcher.matches()) {
                        final String podName = checksumMatcher.group(1);
                        final String checksum = checksumMatcher.group(2);
                        podLock.specChecsums.put(podName, checksum);
                    }
                } else {
                    // TODO: Log
                    System.out.println("PodLockParser: Couldn't find if statement for >" + line + "\n");
                }
            }
        } catch (final IOException e) {
            // TODO: Log
            e.printStackTrace();
            podLock = null;
        }
        return podLock;
    }
}
