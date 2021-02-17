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
package com.synopsys.integration.detect.tool.detector;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.nameversion.DetectorNameVersionHandler;
import com.synopsys.integration.detect.workflow.nameversion.DetectorProjectInfo;
import com.synopsys.integration.detect.workflow.nameversion.DetectorProjectInfoMetadata;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.evaluation.DiscoveryFilter;
import com.synopsys.integration.util.NameVersion;

public class DetectDiscoveryFilter implements DiscoveryFilter {
    private final DetectorNameVersionHandler detectorNameVersionHandler;

    public DetectDiscoveryFilter(EventSystem eventSystem, DetectorNameVersionHandler detectorNameVersionHandler) {
        this.detectorNameVersionHandler = detectorNameVersionHandler;

        eventSystem.registerListener(Event.DiscoveryEnded, this::discoveryEnded);
    }

    public void discoveryEnded(DetectorEvaluation detectorEvaluation) {
        DetectorProjectInfo info = toProjectInfo(detectorEvaluation);
        if (info != null) {
            detectorNameVersionHandler.accept(info);
        }
    }

    @Override
    public boolean shouldDiscover(DetectorEvaluation detectorEvaluation) {
        return detectorNameVersionHandler.willAccept(toMetadataProjectInfo(detectorEvaluation));
    }

    private DetectorProjectInfo toProjectInfo(DetectorEvaluation detectorEvaluation) {
        if (detectorEvaluation.wasDiscoverySuccessful()) {
            String projectName = detectorEvaluation.getDiscovery().getProjectName();
            String projectVersion = detectorEvaluation.getDiscovery().getProjectVersion();

            if (StringUtils.isNotBlank(projectName)) {
                NameVersion nameVersion = new NameVersion(projectName, projectVersion);
                return new DetectorProjectInfo(detectorEvaluation.getDetectorType(), detectorEvaluation.getSearchEnvironment().getDepth(), nameVersion);
            }
        }
        return null;
    }

    private DetectorProjectInfoMetadata toMetadataProjectInfo(DetectorEvaluation detectorEvaluation) {
        return new DetectorProjectInfoMetadata(detectorEvaluation.getDetectorType(), detectorEvaluation.getSearchEnvironment().getDepth());
    }
}
