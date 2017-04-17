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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.blackducksoftware.integration.hub.packman.parser.StreamParser;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.model.WorkspaceProject;

public class WorkspaceProjectParser extends StreamParser<WorkspaceProject> {

    final Pattern NAME_REGEX = Pattern.compile("\\s*productName\\s*=\\s*('|\")(.*)\\1\\s*;");

    final Pattern VERSION_REGEX = Pattern.compile("\\s*CURRENT_PROJECT_VERSION\\s*=\\s*(.*);");

    @Override
    public WorkspaceProject parse(final BufferedReader bufferedReader) {
        WorkspaceProject workspaceProject = null;

        String name = null;
        String version = null;

        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final Matcher nameMathcer = NAME_REGEX.matcher(line);
                final Matcher versionMatcher = VERSION_REGEX.matcher(line);

                if (name == null && nameMathcer.matches()) {
                    name = nameMathcer.group(2).trim();
                } else if (version == null && versionMatcher.matches()) {
                    version = versionMatcher.group(1).trim();
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

        workspaceProject = new WorkspaceProject(name, version);
        return workspaceProject;
    }
}
