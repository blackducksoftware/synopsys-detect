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
package com.blackducksoftware.integration.hub.detect.workflow.project;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.workflow.project.decisions.ArbitraryNameVersionDecision;
import com.blackducksoftware.integration.hub.detect.workflow.project.decisions.NameVersionDecision;
import com.blackducksoftware.integration.hub.detect.workflow.project.decisions.PreferredBomToolDecision;
import com.blackducksoftware.integration.hub.detect.workflow.project.decisions.PreferredBomToolNotFoundDecision;
import com.blackducksoftware.integration.hub.detect.workflow.project.decisions.TooManyPreferredBomToolsFoundDecision;
import com.blackducksoftware.integration.hub.detect.workflow.project.decisions.UniqueBomToolDecision;
import com.blackducksoftware.integration.hub.detect.workflow.project.decisions.UniqueBomToolNotFoundDecision;
import com.blackducksoftware.integration.util.NameVersion;

@Component
public class BomToolNameVersionDecider {
    private final Logger logger = LoggerFactory.getLogger(BomToolNameVersionDecider.class);

    public Optional<NameVersion> decideProjectNameVersion(final List<BomToolProjectInfo> projectNamePossibilities, final Optional<BomToolGroupType> preferredBomToolType) {
        final NameVersionDecision nameVersionDecision = decideProjectNameVersionFromBomTool(projectNamePossibilities, preferredBomToolType);
        nameVersionDecision.printDescription(logger);
        return nameVersionDecision.getChosenNameVersion();
    }

    private NameVersionDecision decideProjectNameVersionFromBomTool(final List<BomToolProjectInfo> projectNamePossibilities, final Optional<BomToolGroupType> preferredBomToolType) {
        final NameVersionDecision decision;

        if (preferredBomToolType.isPresent()) {
            final List<BomToolProjectInfo> possiblePreferred = projectNamePossibilities.stream()
                    .filter(it -> it.getBomToolType() == preferredBomToolType.get())
                    .collect(Collectors.toList());

            final List<BomToolProjectInfo> lowestDepthPossibilities = projectNamesAtLowestDepth(possiblePreferred);

            if (lowestDepthPossibilities.size() == 0) {
                decision = new PreferredBomToolNotFoundDecision(preferredBomToolType.get());
            } else if (lowestDepthPossibilities.size() == 1) {
                decision = new PreferredBomToolDecision(lowestDepthPossibilities.get(0));
            } else {
                decision = new TooManyPreferredBomToolsFoundDecision(preferredBomToolType.get());
            }
        } else {
            final List<BomToolProjectInfo> lowestDepthPossibilities = projectNamesAtLowestDepth(projectNamePossibilities);

            final Map<BomToolGroupType, Long> lowestDepthTypeCounts = lowestDepthPossibilities.stream()
                    .collect(Collectors.groupingBy(it -> it.getBomToolType(), Collectors.counting()));

            final List<BomToolGroupType> singleInstanceLowestDepthBomTools = lowestDepthTypeCounts.entrySet().stream()
                    .filter(it -> it.getValue() == 1)
                    .map(it -> it.getKey())
                    .collect(Collectors.toList());

            if (singleInstanceLowestDepthBomTools.size() == 1) {
                final BomToolGroupType type = singleInstanceLowestDepthBomTools.get(0);
                final BomToolProjectInfo chosen = lowestDepthPossibilities.stream().filter(it -> it.getBomToolType() == type).findFirst().get();
                decision = new UniqueBomToolDecision(chosen);
            } else if (singleInstanceLowestDepthBomTools.size() > 1) {
                decision = decideProjectNameVersionArbitrarily(lowestDepthPossibilities, singleInstanceLowestDepthBomTools);
            } else {
                decision = new UniqueBomToolNotFoundDecision();
            }
        }

        return decision;
    }

    private NameVersionDecision decideProjectNameVersionArbitrarily(final List<BomToolProjectInfo> possibilities, final List<BomToolGroupType> bomToolOptions) {
        final List<BomToolProjectInfo> arbitraryOptions = possibilities.stream()
                .filter(it -> bomToolOptions.contains(it.getBomToolType()))
                .collect(Collectors.toList());

        final Optional<BomToolProjectInfo> chosen = arbitraryOptions.stream()
                .sorted((o1, o2) -> o1.getNameVersion().getName().compareTo(o2.getNameVersion().getName()))
                .findFirst();

        if (chosen.isPresent()) {
            return new ArbitraryNameVersionDecision(chosen.get(), arbitraryOptions);
        } else {
            return new UniqueBomToolNotFoundDecision();
        }
    }

    private List<BomToolProjectInfo> projectNamesAtLowestDepth(final List<BomToolProjectInfo> projectNamePossibilities) {
        List<BomToolProjectInfo> lowestDepthPossibilities = new ArrayList<>();
        final Optional<BomToolProjectInfo> bomToolProjectInfoAtLowestDepth = projectNamePossibilities.stream().max(Comparator.comparingInt(BomToolProjectInfo::getDepth));

        if (bomToolProjectInfoAtLowestDepth.isPresent()) {
            lowestDepthPossibilities = projectNamePossibilities.stream()
                    .filter(it -> it.getDepth() == bomToolProjectInfoAtLowestDepth.get().getDepth())
                    .collect(Collectors.toList());
        }

        return lowestDepthPossibilities;
    }

}
