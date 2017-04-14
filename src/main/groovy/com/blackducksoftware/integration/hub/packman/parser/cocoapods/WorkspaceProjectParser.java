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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.packman.parser.StreamParser;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.model.WorkspaceProject;

public class WorkspaceProjectParser extends StreamParser<WorkspaceProject> {

    final Pattern NAME_REGEX = Pattern.compile("\\{[\\s\\S]*productName\\s*=\\s*('|\")(.*)\\1;");

    final Pattern VERSION_REGEX = Pattern.compile("buildSettings\\s*=\\s*\\{[\\s\\S]*CURRENT_PROJECT_VERSION\\s*=\\s*(.*);");

    @Override
    public WorkspaceProject parse(final BufferedReader bufferedReader) {
        WorkspaceProject workspaceProject = null;

        String name = null;
        String version = null;

        final String proejctText = StringUtils.join(bufferedReader.lines(), "\n");

        final Matcher nameMathcer = NAME_REGEX.matcher(proejctText);
        final Matcher versionMatcher = VERSION_REGEX.matcher(proejctText);

        if (name == null && nameMathcer.matches()) {
            name = nameMathcer.group(2).trim();
        }
        if (version == null && versionMatcher.matches()) {
            version = versionMatcher.group(1).trim();
        }
        workspaceProject = new WorkspaceProject(name, version);
        return workspaceProject;
    }
}
