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
package com.synopsys.integration.detect.workflow.nameversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.detect.workflow.nameversion.decision.ArbitraryNameVersionDecision;
import com.synopsys.integration.detect.workflow.nameversion.decision.NameVersionDecision;
import com.synopsys.integration.detect.workflow.nameversion.decision.UniqueDetectorDecision;
import com.synopsys.integration.detect.workflow.nameversion.decision.UniqueDetectorNotFoundDecision;
import com.synopsys.integration.detector.base.DetectorType;

/*
Originally, name version could be decided after all detectors had ran, there was no benefit calculating the name 'on the fly'.
With the introduction of Project Discovery (and Universal Tools) it does make sense to decide the detector project name as it happens.
The moment we have a detector discovery that we know will be our final choice for project name, we can stop further discovery.
Thus, instead of a 'Decider' that decides at the end, we have a handler that takes incoming detector discoveries.
The handler will accept until it has the 'decided' discovery and then rejects all future discoveries.
This allows discovery to run only the minimum amount of discoveries needed.
 */
public class DetectorNameVersionHandler {
    private final List<DetectorProjectInfo> lowestDepth = new ArrayList<>();

    private final List<DetectorType> lowPriorityDetectorTypes;

    public DetectorNameVersionHandler(List<DetectorType> lowPriorityDetectorTypes) {
        this.lowPriorityDetectorTypes = lowPriorityDetectorTypes;
    }

    public boolean willAccept(DetectorProjectInfoMetadata metadata) {
        if (!lowestDepth.isEmpty()) {
            return metadata.getDepth() <= lowestDepth.get(0).getDepth();
        } else {
            return true;
        }
    }

    public void accept(DetectorProjectInfo projectInfo) {
        if (StringUtils.isBlank(projectInfo.getNameVersion().getName())) {
            return;
        }

        if (!lowestDepth.isEmpty()) {
            int currentDepth = lowestDepth.get(0).getDepth();
            if (projectInfo.getDepth() == currentDepth) {
                lowestDepth.add(projectInfo);
            } else if (projectInfo.getDepth() < currentDepth) {
                lowestDepth.clear();
                lowestDepth.add(projectInfo);
            }
        } else {
            lowestDepth.add(projectInfo);
        }
    }

    public NameVersionDecision finalDecision() {
        List<DetectorProjectInfo> uniqueDetectorsAtLowestDepth = filterUniqueDetectorsOnly(lowestDepth);

        if (uniqueDetectorsAtLowestDepth.size() == 1) {
            return new UniqueDetectorDecision(uniqueDetectorsAtLowestDepth.get(0));
        } else if (uniqueDetectorsAtLowestDepth.size() > 1) {
            return decideProjectNameVersionArbitrarily(lowestDepth);
        } else {
            return new UniqueDetectorNotFoundDecision();
        }
    }

    private NameVersionDecision decideProjectNameVersionArbitrarily(List<DetectorProjectInfo> allPossibilities) {
        List<DetectorProjectInfo> normalPossibilities = Bds.of(allPossibilities)
                                                            .filterNot(it -> lowPriorityDetectorTypes.contains(it.getDetectorType()))
                                                            .toList();

        List<DetectorProjectInfo> chosenPossibilities;
        if (normalPossibilities.isEmpty()) {
            chosenPossibilities = allPossibilities;
        } else {
            chosenPossibilities = normalPossibilities;
        }

        Optional<DetectorProjectInfo> chosenDetectorProjectInfo = chosenPossibilities.stream().min(Comparator.comparing(p -> p.getNameVersion().getName()));

        return chosenDetectorProjectInfo.map(chosen -> {
            List<DetectorProjectInfo> otherOptions = chosenPossibilities.stream()
                                                         .filter(it -> chosen.getDetectorType().equals(it.getDetectorType()))
                                                         .collect(Collectors.toList());
            return (NameVersionDecision) new ArbitraryNameVersionDecision(chosen.getNameVersion(), chosen, otherOptions);
        }).orElse(new UniqueDetectorNotFoundDecision());
    }

    // Return only project info whose detector types appear exactly once.
    protected List<DetectorProjectInfo> filterUniqueDetectorsOnly(List<DetectorProjectInfo> projectNamePossibilities) {
        Map<DetectorType, List<DetectorProjectInfo>> grouped = Bds.of(projectNamePossibilities)
                                                                   .groupBy(DetectorProjectInfo::getDetectorType);

        return grouped.values().stream()
                   .filter(it -> it.size() == 1)
                   .flatMap(Collection::stream)
                   .collect(Collectors.toList());
    }

    public List<DetectorProjectInfo> getLowestDepth() {
        return lowestDepth;
    }
}
