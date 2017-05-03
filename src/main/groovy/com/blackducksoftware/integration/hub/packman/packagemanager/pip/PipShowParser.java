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
package com.blackducksoftware.integration.hub.packman.packagemanager.pip;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;

public class PipShowParser {
    private final Pattern namePattern = Pattern.compile("Name: (.*)");

    private final Pattern versionPattern = Pattern.compile("Version: (.*)");

    private final Pattern requiresPattern = Pattern.compile("Requires: ([\\s\\S]*)");

    public DependencyNode parse(final String pipShowOutput) {
        String name = null;
        String version = null;
        final Set<DependencyNode> children = new HashSet<>();

        final Matcher nameMatcher = namePattern.matcher(pipShowOutput);
        final Matcher versionMatcher = versionPattern.matcher(pipShowOutput);
        final Matcher requiresMatcher = requiresPattern.matcher(pipShowOutput);

        if (nameMatcher.find()) {
            name = nameMatcher.group(1).trim();
        }
        if (versionMatcher.find()) {
            version = versionMatcher.group(1).trim();
        }
        if (requiresMatcher.find()) {
            final String requiresText = requiresMatcher.group(1);
            for (final String required : requiresText.split(",")) {
                final String requiredText = required.trim();
                if (StringUtils.isNotBlank(requiredText)) {
                    final String childName = requiredText;
                    final String childVersion = null;
                    final ExternalId externalId = new NameVersionExternalId(Forge.pypi, childName, childVersion);
                    final DependencyNode childNode = new DependencyNode(childName, childVersion, externalId);
                    children.add(childNode);
                }
            }
        }
        final ExternalId externalId = new NameVersionExternalId(Forge.pypi, name, version);
        final DependencyNode pipNode = new DependencyNode(name, version, externalId, children);
        return pipNode;
    }
}
