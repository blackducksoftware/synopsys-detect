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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detect.workflow.project.decisions.ArbitraryNameVersionDecision;
import com.synopsys.integration.detect.workflow.project.decisions.NameVersionDecision;
import com.synopsys.integration.detect.workflow.project.decisions.PreferredDetectorDecision;
import com.synopsys.integration.detect.workflow.project.decisions.PreferredDetectorNotFoundDecision;
import com.synopsys.integration.detect.workflow.project.decisions.TooManyPreferredDetectorTypesFoundDecision;
import com.synopsys.integration.detect.workflow.project.decisions.UniqueDetectorDecision;
import com.synopsys.integration.detect.workflow.project.decisions.UniqueDetectorNotFoundDecision;
import com.synopsys.integration.util.NameVersion;

public class DetectorNameVersionDecider {
    private final Logger logger = LoggerFactory.getLogger(DetectorNameVersionDecider.class);

    public Optional<NameVersion> decideProjectNameVersion(final List<DetectorProjectInfo> projectNamePossibilities, final DetectorType preferredBomToolType) {
        final NameVersionDecision nameVersionDecision = decideProjectNameVersionFromDetector(projectNamePossibilities, preferredBomToolType);
        nameVersionDecision.printDescription(logger);
        return nameVersionDecision.getChosenNameVersion();
    }

    private NameVersionDecision decideProjectNameVersionFromDetector(final List<DetectorProjectInfo> projectNamePossibilities, final DetectorType preferredBomToolType) {
        final NameVersionDecision decision;

        if (preferredBomToolType != null) {
            final List<DetectorProjectInfo> possiblePreferred = projectNamePossibilities.stream()
                                                                    .filter(it -> it.getDetectorType() == preferredBomToolType)
                                                                    .collect(Collectors.toList());

            final List<DetectorProjectInfo> lowestDepthPossibilities = projectNamesAtLowestDepth(possiblePreferred);

            if (lowestDepthPossibilities.size() == 0) {
                decision = new PreferredDetectorNotFoundDecision(preferredBomToolType);
            } else if (lowestDepthPossibilities.size() == 1) {
                decision = new PreferredDetectorDecision(lowestDepthPossibilities.get(0));
            } else {
                decision = new TooManyPreferredDetectorTypesFoundDecision(preferredBomToolType);
            }
        } else {
            final List<DetectorProjectInfo> lowestDepthPossibilities = projectNamesAtLowestDepth(projectNamePossibilities);

            final Map<DetectorType, Long> lowestDepthTypeCounts = lowestDepthPossibilities.stream()
                                                                      .collect(Collectors.groupingBy(it -> it.getDetectorType(), Collectors.counting()));

            final List<DetectorType> singleInstanceLowestDepthBomTools = lowestDepthTypeCounts.entrySet().stream()
                                                                             .filter(it -> it.getValue() == 1)
                                                                             .map(it -> it.getKey())
                                                                             .collect(Collectors.toList());

            if (singleInstanceLowestDepthBomTools.size() == 1) {
                final DetectorType type = singleInstanceLowestDepthBomTools.get(0);
                final Optional<DetectorProjectInfo> chosen = lowestDepthPossibilities.stream().filter(it -> it.getDetectorType() == type).findFirst();

                if (chosen.isPresent()) {
                    decision = new UniqueDetectorDecision(chosen.get());
                } else {
                    decision = new UniqueDetectorNotFoundDecision();
                }
            } else if (singleInstanceLowestDepthBomTools.size() > 1) {
                decision = decideProjectNameVersionArbitrarily(lowestDepthPossibilities, singleInstanceLowestDepthBomTools);
            } else {
                decision = new UniqueDetectorNotFoundDecision();
            }
        }

        return decision;
    }

    private NameVersionDecision decideProjectNameVersionArbitrarily(final List<DetectorProjectInfo> possibilities, final List<DetectorType> bomToolOptions) {
        final List<DetectorProjectInfo> arbitraryOptions = possibilities.stream()
                                                               .filter(it -> bomToolOptions.contains(it.getDetectorType()))
                                                               .collect(Collectors.toList());

        final Optional<DetectorProjectInfo> chosen = arbitraryOptions.stream()
                                                         .sorted((o1, o2) -> o1.getNameVersion().getName().compareTo(o2.getNameVersion().getName()))
                                                         .findFirst();

        if (chosen.isPresent()) {
            return new ArbitraryNameVersionDecision(chosen.get(), arbitraryOptions);
        } else {
            return new UniqueDetectorNotFoundDecision();
        }
    }

    private List<DetectorProjectInfo> projectNamesAtLowestDepth(final List<DetectorProjectInfo> projectNamePossibilities) {
        List<DetectorProjectInfo> lowestDepthPossibilities = new ArrayList<>();
        final Optional<DetectorProjectInfo> bomToolProjectInfoAtLowestDepth = projectNamePossibilities.stream().min(Comparator.comparingInt(DetectorProjectInfo::getDepth));

        if (bomToolProjectInfoAtLowestDepth.isPresent()) {
            lowestDepthPossibilities = projectNamePossibilities.stream()
                                           .filter(it -> it.getDepth() == bomToolProjectInfoAtLowestDepth.get().getDepth())
                                           .collect(Collectors.toList());
        }

        return lowestDepthPossibilities;
    }

}
