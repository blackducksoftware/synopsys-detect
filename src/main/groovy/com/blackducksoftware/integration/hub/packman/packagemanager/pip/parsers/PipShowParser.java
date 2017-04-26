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
package com.blackducksoftware.integration.hub.packman.packagemanager.pip.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.packman.packagemanager.pip.model.PipPackage;

public class PipShowParser {
    private final Pattern namePattern = Pattern.compile("Name: (.*)");

    private final Pattern versionPattern = Pattern.compile("Version: (.*)");

    private final Pattern requiresPattern = Pattern.compile("Requires: ([\\s\\S]*)");

    public PipPackage parse(final String pipShowOutput) {
        final PipPackage pipPackage = new PipPackage();
        final Matcher nameMatcher = namePattern.matcher(pipShowOutput);
        final Matcher versionMatcher = versionPattern.matcher(pipShowOutput);
        final Matcher requiresMatcher = requiresPattern.matcher(pipShowOutput);

        if (nameMatcher.find()) {
            pipPackage.name = nameMatcher.group(1).trim();
        }
        if (versionMatcher.find()) {
            pipPackage.version = versionMatcher.group(1).trim();
        }
        if (requiresMatcher.find()) {
            final String requiresText = requiresMatcher.group(1);
            final List<String> requires = new ArrayList<>();
            for (final String required : requiresText.split(",")) {
                final String requiredText = required.trim();
                if (StringUtils.isNotBlank(requiredText)) {
                    requires.add(requiredText);
                }
            }
            pipPackage.requires = requires;
        }

        return pipPackage;
    }
}
