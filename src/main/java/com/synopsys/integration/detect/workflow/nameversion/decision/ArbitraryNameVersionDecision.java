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
package com.synopsys.integration.detect.workflow.nameversion.decision;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.workflow.nameversion.DetectorProjectInfo;
import com.synopsys.integration.util.NameVersion;

public class ArbitraryNameVersionDecision extends NameVersionDecision {
    private final DetectorProjectInfo chosenDetector;
    private final List<DetectorProjectInfo> otherDetectors;

    public ArbitraryNameVersionDecision(@Nullable final NameVersion nameVersion, final DetectorProjectInfo chosenDetector, final List<DetectorProjectInfo> otherDetectors) {
        super(nameVersion);
        this.chosenDetector = chosenDetector;
        this.otherDetectors = otherDetectors;
    }

    public DetectorProjectInfo getChosenDetector() {
        return chosenDetector;
    }

    public List<DetectorProjectInfo> getOtherDetectors() {
        return otherDetectors;
    }

    @Override
    public void printDescription(final Logger logger) {
        logger.info("The following project names were found: ");
        logger.info(String.format("\t%s: %s, %s",
            chosenDetector.getDetectorType().name(),
            chosenDetector.getNameVersion().getName(),
            chosenDetector.getNameVersion().getVersion()));
        for (final DetectorProjectInfo projectNamePossibility : otherDetectors) {
            logger.info(String.format("\t%s: %s, %s",
                projectNamePossibility.getDetectorType().name(),
                projectNamePossibility.getNameVersion().getName(),
                projectNamePossibility.getNameVersion().getVersion()
            ));
        }
        logger.info(String.format("Chose to use %s at depth %d for project name and version. Override with %s.",
            chosenDetector.getDetectorType().name(),
            chosenDetector.getDepth(),
             DetectProperties.DETECT_PROJECT_DETECTOR.getProperty().getKey()
        ));

    }
}
