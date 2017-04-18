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
package com.blackducksoftware.integration.hub.packman.parser.cocoapods.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.blackducksoftware.integration.hub.packman.parser.StreamParser;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.CocoapodsPackager;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.model.Podfile;

public class PodfileParser extends StreamParser<Podfile> {

    final Pattern TARGET_REGEX = Pattern.compile("\\s*target *('|\")(.*)\\1 *do\\s*");

    final Pattern TARGET_END_REGEX = Pattern.compile("\\s*end\\s*");

    final Pattern POD_REGEX = Pattern.compile("\\s*pod\\s*('|\")(.*)\\1");

    @Override
    public Podfile parse(final BufferedReader bufferedReader) {
        Podfile podfile = new Podfile();

        DependencyNode currentTarget = null;

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                final Matcher targetMatcher = TARGET_REGEX.matcher(line);
                final Matcher targetEndMatcher = TARGET_END_REGEX.matcher(line);
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

                if (StringUtils.isBlank(line)) {

                } else if (targetMatcher.matches()) {
                    final String targetName = targetMatcher.group(2);
                    final String targetVersion = DateTime.now().toString("MM_dd_YYYY_HH:mm:Z");
                    final ExternalId externalId = new NameVersionExternalId(Forge.cocoapods, targetName, targetVersion);
                    final DependencyNode target = new DependencyNode(targetName, targetVersion, externalId, new ArrayList<DependencyNode>());
                    currentTarget = target;
                    podfile.targets.add(currentTarget);
                } else if (podMatcher.find() && currentTarget != null) {
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
