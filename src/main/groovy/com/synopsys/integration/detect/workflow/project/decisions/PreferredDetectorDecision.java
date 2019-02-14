/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.project.decisions;

import java.util.Optional;

import org.slf4j.Logger;

import com.synopsys.integration.detect.workflow.project.DetectorProjectInfo;
import com.synopsys.integration.util.NameVersion;

public class PreferredDetectorDecision extends NameVersionDecision {
    private final DetectorProjectInfo chosenDetectorProjectInfo;

    public PreferredDetectorDecision(final DetectorProjectInfo chosenDetectorProjectInfo) {
        this.chosenDetectorProjectInfo = chosenDetectorProjectInfo;
    }

    @Override
    public Optional<NameVersion> getChosenNameVersion() {
        return Optional.of(chosenDetectorProjectInfo.getNameVersion());
    }

    @Override
    public void printDescription(final Logger logger) {
        logger.info("Using preferred bom tool project info from " + chosenDetectorProjectInfo.getDetectorType().toString() + " found at depth " + Integer.toString(chosenDetectorProjectInfo.getDepth()) + " as project info.");
    }

}
