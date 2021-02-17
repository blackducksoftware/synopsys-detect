/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.project;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.enumeration.DefaultVersionNameScheme;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.util.NameVersion;

public class ProjectNameVersionDecider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProjectNameVersionOptions projectVersionOptions;

    public ProjectNameVersionDecider(ProjectNameVersionOptions projectVersionOptions) {
        this.projectVersionOptions = projectVersionOptions;
    }

    public NameVersion decideProjectNameVersion(List<DetectTool> preferredDetectTools, List<DetectToolProjectInfo> detectToolProjectInfo) {
        Optional<DetectToolProjectInfo> chosenTool = decideToolProjectInfo(preferredDetectTools, detectToolProjectInfo);
        Optional<String> chosenToolName = chosenTool.map(DetectToolProjectInfo::getSuggestedNameVersion).map(NameVersion::getName);
        Optional<String> chosenToolVersion = chosenTool.map(DetectToolProjectInfo::getSuggestedNameVersion).map(NameVersion::getVersion);

        String decidedProjectName;
        if (StringUtils.isNotBlank(projectVersionOptions.overrideProjectName)) {
            decidedProjectName = projectVersionOptions.overrideProjectName;
        } else if (chosenToolName.isPresent()) {
            decidedProjectName = chosenToolName.get();
        } else {
            logger.debug("A project name could not be decided. Using the name of the source path.");
            decidedProjectName = projectVersionOptions.sourcePathName;
        }

        String decidedProjectVersionName;
        if (StringUtils.isNotBlank(projectVersionOptions.overrideProjectVersionName)) {
            decidedProjectVersionName = projectVersionOptions.overrideProjectVersionName;
        } else if (chosenToolVersion.isPresent()) {
            decidedProjectVersionName = chosenToolVersion.get();
        } else if (DefaultVersionNameScheme.TIMESTAMP.equals(projectVersionOptions.defaultProjectVersionScheme)) {
            logger.debug("A project version name could not be decided. Using the current timestamp.");
            String timeformat = projectVersionOptions.defaultProjectVersionFormat;
            decidedProjectVersionName = DateTimeFormatter.ofPattern(timeformat).withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC));
        } else {
            logger.debug("A project version name could not be decided. Using the default version text.");
            decidedProjectVersionName = projectVersionOptions.defaultProjectVersionText;
        }

        return new NameVersion(decidedProjectName, decidedProjectVersionName);
    }

    private Optional<DetectToolProjectInfo> decideToolProjectInfo(List<DetectTool> preferredDetectTools, List<DetectToolProjectInfo> detectToolProjectInfo) {
        Optional<DetectToolProjectInfo> chosenTool = Optional.empty();

        for (DetectTool tool : preferredDetectTools) {
            chosenTool = detectToolProjectInfo.stream()
                             .filter(it -> it.getDetectTool().equals(tool))
                             .findFirst();

            if (chosenTool.isPresent()) {
                logger.debug(String.format("Using the first ordered tool with project info: %s", tool.toString()));
                break;
            }
        }

        return chosenTool;
    }
}
