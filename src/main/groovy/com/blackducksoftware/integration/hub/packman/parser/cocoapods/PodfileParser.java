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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.blackducksoftware.integration.hub.packman.parser.StreamParser;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.model.Podfile;

public class PodfileParser extends StreamParser<Podfile> {

    final Pattern PLATFORM_REGEX = Pattern.compile("platform :(.*)\\s*");

    final Pattern TARGET_REGEX = Pattern.compile("target *('|\")(.*)\\1 *do\\s*");

    final Pattern TARGET_END_REGEX = Pattern.compile("end\\s*");

    final Pattern FRAMEWORKS_REGEX = Pattern.compile(" *use_frameworks!");

    final Pattern POD_REGEX = Pattern.compile("\\s*pod *('|\")(.*)\\1, *('|\")(.*)\\3\\s*");

    @Override
    public Podfile parse(final BufferedReader bufferedReader) {
        Podfile podfile = new Podfile();

        DependencyNode currentTarget = null;

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                final Matcher platformMatcher = PLATFORM_REGEX.matcher(line);
                final Matcher targetMatcher = TARGET_REGEX.matcher(line);
                final Matcher targetEndMatcher = TARGET_END_REGEX.matcher(line);
                final Matcher frameworksMatcher = FRAMEWORKS_REGEX.matcher(line);
                final Matcher podMatcher = POD_REGEX.matcher(line);

                // Handle comments
                if (line.contains("#")) {
                    final String[] sections = line.split("#");
                    if (sections.length > 0) {
                        line = sections[0].trim();
                    } else {
                        line = "";
                    }
                }

                if (line.isEmpty()) {

                } else if (platformMatcher.matches()) {
                    podfile.platform = platformMatcher.group(1);
                    currentTarget = null;
                } else if (targetMatcher.matches()) {
                    final String targetName = targetMatcher.group(2);
                    final ExternalId externalId = new NameVersionExternalId(Forge.cocoapods, targetName, null);
                    final DependencyNode target = new DependencyNode(targetName, null, externalId, new ArrayList<DependencyNode>());
                    currentTarget = target;
                    podfile.targets.add(currentTarget);
                } else if (frameworksMatcher.matches()) {
                    podfile.useFramworks = frameworksMatcher.group();
                } else if (podMatcher.matches() && currentTarget != null) {
                    final DependencyNode pod = CocoapodsPackager.createPodNodeFromGroups(podMatcher, 2, 4);
                    currentTarget.children.add(pod);
                } else if (targetEndMatcher.matches()) {
                    currentTarget = null;
                } else {
                    // TODO: Log
                    System.out.println("PodfileParser: Couldn't find if statement for >" + line + "\n");
                }
            }
        } catch (final IOException e) {
            // TODO: Log
            e.printStackTrace();
            podfile = null;
        }
        return podfile;
    }
}
