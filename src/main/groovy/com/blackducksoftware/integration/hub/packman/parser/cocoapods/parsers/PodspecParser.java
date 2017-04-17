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
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.CocoapodsPackager;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.model.Podspec;

public class PodspecParser extends StreamParser<Podspec> {

    final Pattern NAME_REGEX = Pattern.compile(".*\\.name\\s*=\\s*('|\")(.*)\\1.*");

    final Pattern VERSION_REGEX = Pattern.compile(".*\\.version\\s*=\\s*('|\")(.*)\\1.*");

    @Override
    public Podspec parse(final BufferedReader bufferedReader) {
        Podspec podspec = null;

        String name = null;
        String version = null;

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                final Matcher nameMathcer = NAME_REGEX.matcher(line);
                final Matcher versionMatcher = VERSION_REGEX.matcher(line);

                line = processSingleLineComments(line, CocoapodsPackager.COMMENTS);

                if (line.isEmpty()) {

                } else if (name == null && nameMathcer.matches()) {
                    name = nameMathcer.group(2).trim();
                } else if (version == null && versionMatcher.matches()) {
                    version = versionMatcher.group(2).trim();
                } else if (name != null && version != null) {
                    break;
                }
            }
            podspec = new Podspec(name, version);
        } catch (final IOException e) {
            // TODO: Log
            e.printStackTrace();
        }
        return podspec;
    }
}
