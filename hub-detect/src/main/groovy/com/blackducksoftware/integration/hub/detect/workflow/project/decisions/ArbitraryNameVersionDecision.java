/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.workflow.project.decisions;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import com.blackducksoftware.integration.hub.detect.workflow.project.DetectorProjectInfo;
import com.synopsys.integration.util.NameVersion;

public class ArbitraryNameVersionDecision extends NameVersionDecision {
    private final DetectorProjectInfo chosenDetector;
    private final List<DetectorProjectInfo> otherDetectors;

    public ArbitraryNameVersionDecision(final DetectorProjectInfo chosenDetector, final List<DetectorProjectInfo> otherDetectors) {
        this.chosenDetector = chosenDetector;
        this.otherDetectors = otherDetectors;
    }

    @Override
    public Optional<NameVersion> getChosenNameVersion() {
        return Optional.of(chosenDetector.getNameVersion());
    }

    @Override
    public void printDescription(final Logger logger) {
        logger.info("Multiple unique detector types were found.");
        logger.info("The following project names were found: ");
        for (final DetectorProjectInfo projectNamePossibility : otherDetectors) {
            logger.info(projectNamePossibility.getDetectorType().toString() + ": " + projectNamePossibility.getNameVersion().getName());
        }
        logger.info("Chose to use " + chosenDetector.getDetectorType() + " at depth " + chosenDetector.getDepth() + " for project name and version.");
        logger.info("To specify a different detector type you can specify the project type override.");
    }

}
