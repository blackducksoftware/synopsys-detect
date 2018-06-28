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
package com.blackducksoftware.integration.hub.detect.manager.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.manager.project.result.ArbitrarilyChosenProjectInfoResult;
import com.blackducksoftware.integration.hub.detect.manager.project.result.NoUniqueUnchosenProjectInfoResult;
import com.blackducksoftware.integration.hub.detect.manager.project.result.OneUniqueChosenProjectInfoResult;
import com.blackducksoftware.integration.hub.detect.manager.project.result.PreferredMultipleUnchosenProjectInfoResult;
import com.blackducksoftware.integration.hub.detect.manager.project.result.PreferredNotFoundUnchosenProjectInfoResult;
import com.blackducksoftware.integration.hub.detect.manager.project.result.PreferredSingleChosenProjectInfoResult;
import com.blackducksoftware.integration.hub.detect.manager.project.result.ProjectInfoResult;
import com.blackducksoftware.integration.util.NameVersion;

@Component
public class BomToolProjectInfoDecider {
    private final Logger logger = LoggerFactory.getLogger(BomToolProjectInfoDecider.class);

    public Optional<NameVersion> decideProjectInfo(final List<BomToolProjectInfo> projectNamePossibilities, final Optional<BomToolGroupType> preferredBomToolType) {
        final ProjectInfoResult chosenBomToolProjectInfo = decideBomToolInfo(projectNamePossibilities, preferredBomToolType);
        chosenBomToolProjectInfo.printDescription(logger);
        if (chosenBomToolProjectInfo.getChosenNameVersion().isPresent()) {
            return chosenBomToolProjectInfo.getChosenNameVersion();
        } else {
            return Optional.empty();
        }
    }

    private ProjectInfoResult decideBomToolInfo(final List<BomToolProjectInfo> projectNamePossibilities, final Optional<BomToolGroupType> preferredBomToolType) {
        if (preferredBomToolType.isPresent()) {
            final List<BomToolProjectInfo> possiblePreferred = projectNamePossibilities.stream()
                    .filter(it -> it.getBomToolType() == preferredBomToolType.get())
                    .collect(Collectors.toList());

            final List<BomToolProjectInfo> lowestDepthPossibilities = projectNamesAtLowestDepth(possiblePreferred);

            if (lowestDepthPossibilities.size() == 0) {
                return new PreferredNotFoundUnchosenProjectInfoResult(preferredBomToolType.get());
            } else if (lowestDepthPossibilities.size() == 1) {
                return new PreferredSingleChosenProjectInfoResult(lowestDepthPossibilities.get(0));
            } else {
                return new PreferredMultipleUnchosenProjectInfoResult(preferredBomToolType.get());
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
                return new OneUniqueChosenProjectInfoResult(chosen);
            } else if (singleInstanceLowestDepthBomTools.size() > 1) {
                return arbitrarilyDecide(lowestDepthPossibilities, singleInstanceLowestDepthBomTools);
            } else {
                return new NoUniqueUnchosenProjectInfoResult();
            }
        }
    }

    private ProjectInfoResult arbitrarilyDecide(final List<BomToolProjectInfo> possibilities, final List<BomToolGroupType> bomToolOptions) {
        final List<BomToolProjectInfo> arbitraryOptions = possibilities.stream()
                .filter(it -> bomToolOptions.contains(it.getBomToolType()))
                .collect(Collectors.toList());

        final Optional<BomToolProjectInfo> chosen = arbitraryOptions.stream()
                .sorted((o1, o2) -> o1.getNameVersion().getName().compareTo(o2.getNameVersion().getName()))
                .findFirst();

        if (chosen.isPresent()) {
            return new ArbitrarilyChosenProjectInfoResult(chosen.get(), arbitraryOptions);
        } else {
            return new NoUniqueUnchosenProjectInfoResult();
        }
    }

    private List<BomToolProjectInfo> projectNamesAtLowestDepth(final List<BomToolProjectInfo> projectNamePossibilities) {
        final Optional<Integer> lowestDepth = projectNamePossibilities.stream()
                .map(it -> it.getDepth())
                .min(Integer::compare);

        if (lowestDepth.isPresent()) {
            final List<BomToolProjectInfo> lowestDepthPossibilities = projectNamePossibilities.stream()
                    .filter(it -> it.getDepth() == lowestDepth.get())
                    .collect(Collectors.toList());
            return lowestDepthPossibilities;
        } else {
            return new ArrayList<>();
        }
    }

}
