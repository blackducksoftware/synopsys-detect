/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.cran.parse;

import java.util.List;

import com.synopsys.integration.util.NameVersion;

public class PackratDescriptionFileParser {
    private static final String PACKAGE_TOKEN = "Package:";
    private static final String VERSION_TOKEN = "Version:";

    public NameVersion getProjectNameVersion(final List<String> descriptionFileLines, final String defaultProjectName, final String defaultProjectVersion) {
        final NameVersion nameVersion = new NameVersion(defaultProjectName, defaultProjectVersion);

        for (final String rawLine : descriptionFileLines) {
            final String line = rawLine.trim();

            if (line.startsWith(PACKAGE_TOKEN)) {
                final String projectName = line.replace(PACKAGE_TOKEN, "").trim();
                nameVersion.setName(projectName);
            } else if (line.startsWith(VERSION_TOKEN)) {
                final String projectVersion = line.replace(VERSION_TOKEN, "").trim();
                nameVersion.setVersion(projectVersion);
            }
        }

        return nameVersion;
    }
}
