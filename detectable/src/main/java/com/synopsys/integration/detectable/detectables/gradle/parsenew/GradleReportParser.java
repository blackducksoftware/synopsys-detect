/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectables.gradle.parsenew;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.gradle.model.GradleConfiguration;
import com.synopsys.integration.detectable.detectables.gradle.model.GradleReport;

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

    private final ExternalIdFactory externalIdFactory;

    private GradleReportConfigurationParser gradleReportConfigurationParser = new GradleReportConfigurationParser();

    public GradleReportParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public GradleReport parseReport(final File reportFile) {
        GradleReport gradleReport = new GradleReport();
        boolean processingMetaData = false;
        List<String> configurationLines = new ArrayList<String>();
        try (FileInputStream dependenciesInputStream = new FileInputStream(reportFile); BufferedReader reader = new BufferedReader(new InputStreamReader(dependenciesInputStream, StandardCharsets.UTF_8));) {
            while (reader.ready()) {
                final String line = reader.readLine();
                /**
                 * The meta data section will be at the end of the file after all of the "gradle dependencies" output
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
                    if (line.startsWith(PROJECT_PATH_PREFIX)) {
                        gradleReport.projectSourcePath = line.substring(PROJECT_PATH_PREFIX.length()).trim();
                    } else if (line.startsWith(PROJECT_GROUP_PREFIX)) {
                        gradleReport.projectGroup = line.substring(PROJECT_GROUP_PREFIX.length()).trim();
                    } else if (line.startsWith(PROJECT_NAME_PREFIX)) {
                        gradleReport.projectName = line.substring(PROJECT_NAME_PREFIX.length()).trim();
                    } else if (line.startsWith(PROJECT_VERSION_PREFIX)) {
                        gradleReport.projectVersionName = line.substring(PROJECT_VERSION_PREFIX.length()).trim();
                    }
                    continue;
                }

                if (StringUtils.isBlank(line)) {//TODO: Does this handle the 'header block' and 'footer block' of the output the same way?
                    if (configurationLines.size() > 1) {
                        String header = configurationLines.get(0);
                        List<String> dependencyTree = configurationLines.stream().skip(1).collect(Collectors.toList());
                        GradleConfiguration configuration = gradleReportConfigurationParser.parse(header, dependencyTree);
                        gradleReport.configurations.add(configuration);
                    }
                    configurationLines.clear();
                } else {
                    configurationLines.add(line);
                }

            }
        } catch (final IOException e) {
            gradleReport = null; //TODO?
        }

        return gradleReport;
    }
}
