/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.util.List;
import java.util.stream.Collectors;

import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleConfiguration;

public class GradleReportConfigurationParser {
    private final GradleReportLineParser parser = new GradleReportLineParser();

    public GradleConfiguration parse(final String header, final List<String> dependencyLines) {
        final GradleConfiguration configuration = new GradleConfiguration();

        configuration.setName(parseConfigurationName(header));

        configuration.setChildren(dependencyLines.stream()
                                      .map(parser::parseLine)
                                      .collect(Collectors.toList()));

        return configuration;
    }

    private String parseConfigurationName(final String header) {
        if (header.contains(" - ")) {
            return header.substring(0, header.indexOf(" - ")).trim();
        } else {
            return header.trim();
        }
    }

}
