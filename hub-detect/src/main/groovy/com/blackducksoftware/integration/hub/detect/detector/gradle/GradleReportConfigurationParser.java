/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.detector.gradle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class GradleReportConfigurationParser {
    private final Logger logger = LoggerFactory.getLogger(GradleReportConfigurationParser.class);

    private boolean processingConfiguration;
    private boolean processingProjectTree;
    private String lineThatMayContainConfigurationName;
    private GradleReportLine gradleReportLine;

    public Dependency parseDependency(final ExternalIdFactory externalIdFactory, final String line) {
        if (shouldParseLine(line)) {
            final Dependency dependency = gradleReportLine.createDependencyNode(externalIdFactory);
            return dependency;
        }
        return null;
    }

    public int getTreeLevel() {
        return gradleReportLine.getTreeLevel();
    }

    public boolean shouldParseLine(final String line) {
        gradleReportLine = new GradleReportLine(line);

        if (!processingConfiguration) {
            if (gradleReportLine.isRootLevel()) {
                processingConfiguration = true;
                final String configurationName = extractConfigurationName();
                logger.info(String.format("Started processing of configuration: %s", configurationName));
            } else {
                lineThatMayContainConfigurationName = line;
                return false;
            }
        }

        if (processingProjectTree && !gradleReportLine.isRootLevel()) {
            return false;
        }

        if (gradleReportLine.isRootLevelProject()) {
            processingProjectTree = true;
            return false;
        }

        processingProjectTree = false;

        return processingConfiguration && !processingProjectTree;
    }

    public GradleReportLine getLineToProcess() {
        return gradleReportLine;
    }

    private String extractConfigurationName() {
        if (lineThatMayContainConfigurationName.contains(" - ")) {
            return lineThatMayContainConfigurationName.substring(0, lineThatMayContainConfigurationName.indexOf(" - ")).trim();
        } else {
            return lineThatMayContainConfigurationName.trim();
        }
    }

}
