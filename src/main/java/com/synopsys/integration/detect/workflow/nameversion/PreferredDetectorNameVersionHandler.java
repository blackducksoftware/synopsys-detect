/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
import com.synopsys.integration.util.NameVersion;

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

    public PreferredDetectorNameVersionHandler(DetectorType preferredDetectorType) {
        super(Collections.emptyList());
        this.preferredDetectorType = preferredDetectorType;
    }

    @Override
    public boolean willAccept(DetectorProjectInfoMetadata metadata) {
        if (metadata.getDetectorType().equals(preferredDetectorType)) {
            return super.willAccept(metadata);
        } else {
            return false;
        }
    }

    @Override
    public void accept(DetectorProjectInfo projectInfo) {
        if (projectInfo.getDetectorType().equals(preferredDetectorType)) {
            super.accept(projectInfo);
        }
    }

    //TODO- do we want to apply git here?
    @Override
    public void applyGitNameVersion(NameVersion gitNameVersion) {
        getLowestDepth().add(new DetectorProjectInfo(DetectorType.valueOf("N/A"), 0, gitNameVersion));
    }

    @NotNull
    @Override
    public NameVersionDecision finalDecision() {
        List<DetectorProjectInfo> uniqueDetectorsAtLowestDepth = this.filterUniqueDetectorsOnly(getLowestDepth());

        if (uniqueDetectorsAtLowestDepth.isEmpty()) {
            return new PreferredDetectorNotFoundDecision(preferredDetectorType);
        } else if (uniqueDetectorsAtLowestDepth.size() == 1) {
            return new PreferredDetectorDecision(uniqueDetectorsAtLowestDepth.get(0));
        } else {
            return new TooManyPreferredDetectorTypesFoundDecision(preferredDetectorType);
        }
    }
}
