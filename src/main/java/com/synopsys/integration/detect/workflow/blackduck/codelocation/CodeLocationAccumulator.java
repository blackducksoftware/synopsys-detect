/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.codelocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.synopsys.integration.blackduck.codelocation.CodeLocationBatchOutput;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;

public class CodeLocationAccumulator {
    private final List<AccumulatedCodeLocationData> waitableCodeLocationData = new ArrayList<>();
    private final Set<String> nonWaitableCodeLocations = new HashSet<>();

    public void addWaitableCodeLocation(CodeLocationCreationData<? extends CodeLocationBatchOutput<?>> creationData) {
        waitableCodeLocationData.add(new AccumulatedCodeLocationData(creationData.getOutput().getExpectedNotificationCount(), creationData.getOutput().getSuccessfulCodeLocationNames(), creationData.getNotificationTaskRange()));
    }

    public void addNonWaitableCodeLocation(Set<String> names) {
        nonWaitableCodeLocations.addAll(names);
    }

    public List<AccumulatedCodeLocationData> getWaitableCodeLocations() {
        return waitableCodeLocationData;
    }

    public Set<String> getNonWaitableCodeLocations() {
        return nonWaitableCodeLocations;
    }
}
