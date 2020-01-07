/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.DetectTool;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.util.NameVersion;

public class ProjectNameVersionDecider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProjectNameVersionOptions projectVersionOptions;

    public ProjectNameVersionDecider(final ProjectNameVersionOptions projectVersionOptions) {
        this.projectVersionOptions = projectVersionOptions;
    }

    public NameVersion decideProjectNameVersion(final List<DetectTool> preferredDetectTools, final List<DetectToolProjectInfo> detectToolProjectInfo) throws DetectUserFriendlyException {
        Optional<String> decidedProjectName = Optional.empty();
        Optional<String> decidedProjectVersion = Optional.empty();

        if (StringUtils.isNotBlank(projectVersionOptions.overrideProjectName)) {
            decidedProjectName = Optional.of(projectVersionOptions.overrideProjectName);
        }

        if (StringUtils.isNotBlank(projectVersionOptions.overrideProjectVersionName)) {
            decidedProjectVersion = Optional.of(projectVersionOptions.overrideProjectVersionName);
        }

        final Optional<DetectToolProjectInfo> chosenTool = decideToolProjectInfo(preferredDetectTools, detectToolProjectInfo);
        if (chosenTool.isPresent()) {
            if (!decidedProjectName.isPresent()) {
                final String suggestedName = chosenTool.get().getSuggestedNameVersion().getName();
                if (StringUtils.isNotBlank(suggestedName)) {
                    decidedProjectName = Optional.of(suggestedName);
                }
            }
            if (!decidedProjectVersion.isPresent()) {
                final String suggestedVersion = chosenTool.get().getSuggestedNameVersion().getVersion();
                if (StringUtils.isNotBlank(suggestedVersion)) {
                    decidedProjectVersion = Optional.of(suggestedVersion);
                }
            }
        }

        if (!decidedProjectName.isPresent()) {
            logger.debug("A project name could not be decided. Using the name of the source path.");
            decidedProjectName = Optional.of(projectVersionOptions.sourcePathName);
        }

        if (!decidedProjectVersion.isPresent()) {
            if ("timestamp".equals(projectVersionOptions.defaultProjectVersionScheme)) {
                logger.debug("A project version name could not be decided. Using the current timestamp.");
                final String timeformat = projectVersionOptions.defaultProjectVersionFormat;
                final String timeString = DateTimeFormatter.ofPattern(timeformat).withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC));
                decidedProjectVersion = Optional.of(timeString);
            } else {
                logger.debug("A project version name could not be decided. Using the default version text.");
                decidedProjectVersion = Optional.of(projectVersionOptions.defaultProjectVersionText);
            }
        }

        return new NameVersion(decidedProjectName.get(), decidedProjectVersion.get());
    }

    private Optional<DetectToolProjectInfo> findProjectInfoForTool(final DetectTool tool, final List<DetectToolProjectInfo> detectToolProjectInfo) {
        return detectToolProjectInfo.stream()
                   .filter(it -> it.getDetectTool().equals(tool))
                   .findFirst();
    }

    private Optional<DetectToolProjectInfo> decideToolProjectInfo(final List<DetectTool> preferredDetectTools, final List<DetectToolProjectInfo> detectToolProjectInfo) throws DetectUserFriendlyException {
        Optional<DetectToolProjectInfo> chosenTool = Optional.empty();
        for (final DetectTool tool : preferredDetectTools) {
            chosenTool = findProjectInfoForTool(tool, detectToolProjectInfo);

            if (chosenTool.isPresent()) {
                logger.debug("Using the first ordered tool with project info: " + tool.toString());
                break;
            }
        }

        return chosenTool;
    }
}
