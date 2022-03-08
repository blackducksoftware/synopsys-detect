package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleConfiguration;

public class GradleReportConfigurationParser {
    private static final String UNRESOLVED_SUFFIX = "(n)";

    private final GradleReportLineParser parser = new GradleReportLineParser();

    public GradleConfiguration parse(String header, List<String> dependencyLines) {
        GradleConfiguration configuration = new GradleConfiguration();

        configuration.setName(parseConfigurationName(header));
        configuration.setUnresolved(parseUnresolved(header));
        configuration.setChildren(dependencyLines.stream()
            .map(parser::parseLine)
            .collect(Collectors.toList()));

        return configuration;
    }

    private boolean parseUnresolved(String header) {
        return StringUtils.endsWith(header, UNRESOLVED_SUFFIX);
    }

    private String parseConfigurationName(String header) {
        if (header.contains(" - ")) {
            return header.substring(0, header.indexOf(" - ")).trim();
        } else {
            return header.trim();
        }
    }

}
