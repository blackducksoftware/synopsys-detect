/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleConfiguration;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleReport;

public class GradleReportParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String PROJECT_PATH_PREFIX = "projectPath:";
    public static final String PROJECT_GROUP_PREFIX = "projectGroup:";
    public static final String PROJECT_NAME_PREFIX = "projectName:";
    public static final String PROJECT_VERSION_PREFIX = "projectVersion:";
    public static final String ROOT_PROJECT_NAME_PREFIX = "rootProjectName:";
    public static final String ROOT_PROJECT_VERSION_PREFIX = "rootProjectVersion:";
    public static final String DETECT_META_DATA_HEADER = "DETECT META DATA START";
    public static final String DETECT_META_DATA_FOOTER = "DETECT META DATA END";

    private final GradleReportConfigurationParser gradleReportConfigurationParser = new GradleReportConfigurationParser();

    public Optional<GradleReport> parseReport(final File reportFile) {
        GradleReport gradleReport = new GradleReport();
        boolean processingMetaData = false;
        final List<String> configurationLines = new ArrayList<>();
        try (final InputStream dependenciesInputStream = new FileInputStream(reportFile); final BufferedReader reader = new BufferedReader(new InputStreamReader(dependenciesInputStream, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                final String line = reader.readLine();
                /*
                  The meta data section will be at the end of the file after all of the "gradle dependencies" output
                 */
                if (line.startsWith(DETECT_META_DATA_HEADER)) {
                    processingMetaData = true;
                    continue;
                }
                if (line.startsWith(DETECT_META_DATA_FOOTER)) {
                    processingMetaData = false;
                    continue;
                }
                if (processingMetaData) {
                    setGradleReportInfo(gradleReport, line);
                    continue;
                }

                if (StringUtils.isBlank(line)) {
                    parseConfigurationLines(configurationLines, gradleReport);
                    configurationLines.clear();
                } else {
                    configurationLines.add(line);
                }

            }

            parseConfigurationLines(configurationLines, gradleReport);
        } catch (final Exception e) {
            logger.debug(String.format("Failed to read report file: %s", reportFile.getAbsolutePath()), e);
            gradleReport = null;
        }

        return Optional.ofNullable(gradleReport);
    }

    private void setGradleReportInfo(GradleReport gradleReport, String line) {
        if (line.startsWith(PROJECT_PATH_PREFIX)) {
            gradleReport.setProjectSourcePath(line.substring(PROJECT_PATH_PREFIX.length()).trim());
        } else if (line.startsWith(PROJECT_GROUP_PREFIX)) {
            gradleReport.setProjectGroup(line.substring(PROJECT_GROUP_PREFIX.length()).trim());
        } else if (line.startsWith(PROJECT_NAME_PREFIX)) {
            gradleReport.setProjectName(line.substring(PROJECT_NAME_PREFIX.length()).trim());
        } else if (line.startsWith(PROJECT_VERSION_PREFIX)) {
            gradleReport.setProjectVersionName(line.substring(PROJECT_VERSION_PREFIX.length()).trim());
        }
    }

    private void parseConfigurationLines(final List<String> configurationLines, final GradleReport gradleReport) {
        if (configurationLines.size() > 1 && isConfigurationHeader(configurationLines)) {
            final String header = configurationLines.get(0);
            final List<String> dependencyTree = configurationLines.stream().skip(1).collect(Collectors.toList());
            final GradleConfiguration configuration = gradleReportConfigurationParser.parse(header, dependencyTree);
            gradleReport.getConfigurations().add(configuration);
        }
    }

    private boolean isConfigurationHeader(final List<String> lines) {
        if (lines.get(0).contains(" - ")) {
            return true;
        } else {
            return StringUtils.isAlphanumeric(lines.get(0));
        }
    }
}
