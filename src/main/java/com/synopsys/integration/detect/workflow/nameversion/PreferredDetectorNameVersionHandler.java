/**
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
package com.synopsys.integration.detect.workflow.nameversion;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detect.workflow.nameversion.decision.NameVersionDecision;
import com.synopsys.integration.detect.workflow.nameversion.decision.PreferredDetectorDecision;
import com.synopsys.integration.detect.workflow.nameversion.decision.PreferredDetectorNotFoundDecision;
import com.synopsys.integration.detect.workflow.nameversion.decision.TooManyPreferredDetectorTypesFoundDecision;
import com.synopsys.integration.detector.base.DetectorType;

/*
Originally, name version could be decided after all detectors had ran, there was no benefit calculating the name 'on the fly'.
With the introduction of Project Discovery (and Universal Tools) it does make sense to decide the detector project name as it happens.
The moment we have a detector discovery that we know will be our final choice for project name, we can stop further discovery.
Thus, instead of a 'Decider' that decides at the end, we have a handler that takes incoming detector discoveries.
The handler will accept until it has the 'decided' discovery and then rejects all future discoveries.
This allows discovery to run only the minimum amount of discoveries needed.
 */
public class PreferredDetectorNameVersionHandler extends DetectorNameVersionHandler {
    private final DetectorType preferredDetectorType;

    public PreferredDetectorNameVersionHandler(final DetectorType preferredDetectorType) {
        super(Collections.emptyList());
        this.preferredDetectorType = preferredDetectorType;
    }

    @Override
    public boolean willAccept(final DetectorProjectInfoMetadata metadata) {
        if (metadata.getDetectorType().equals(preferredDetectorType)) {
            return super.willAccept(metadata);
        } else {
            return false;
        }
    }

    @Override
    public void accept(final DetectorProjectInfo projectInfo) {
        if (projectInfo.getDetectorType().equals(preferredDetectorType)) {
            super.accept(projectInfo);
        }
    }

    @NotNull
    @Override
    public NameVersionDecision finalDecision() {
        final List<DetectorProjectInfo> uniqueDetectorsAtLowestDepth = this.filterUniqueDetectorsOnly(getLowestDepth());

        if (uniqueDetectorsAtLowestDepth.isEmpty()) {
            return new PreferredDetectorNotFoundDecision(preferredDetectorType);
        } else if (uniqueDetectorsAtLowestDepth.size() == 1) {
            return new PreferredDetectorDecision(uniqueDetectorsAtLowestDepth.get(0));
        } else {
            return new TooManyPreferredDetectorTypesFoundDecision(preferredDetectorType);
        }
    }
}
