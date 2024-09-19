package com.blackduck.integration.detect.workflow.blackduck.codelocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blackduck.integration.blackduck.codelocation.CodeLocationBatchOutput;
import com.blackduck.integration.blackduck.codelocation.CodeLocationCreationData;
import com.blackduck.integration.detect.configuration.enumeration.DetectTool;

public class CodeLocationAccumulator {
    private final List<WaitableCodeLocationData> waitableCodeLocationData = new ArrayList<>();
    private final Set<String> nonWaitableCodeLocations = new HashSet<>();

    public void addWaitableCodeLocations(DetectTool detectTool, CodeLocationCreationData<? extends CodeLocationBatchOutput<?>> creationData) {
        addWaitableCodeLocations(new WaitableCodeLocationData(detectTool,
            creationData.getOutput().getExpectedNotificationCount(),
            creationData.getOutput().getSuccessfulCodeLocationNames(),
            creationData.getNotificationTaskRange()
        ));
    }

    public void addWaitableCodeLocations(WaitableCodeLocationData codeLocationData) {
        waitableCodeLocationData.add(codeLocationData);
    }

    public void addNonWaitableCodeLocation(Set<String> names) {
        nonWaitableCodeLocations.addAll(names);
    }

    public List<WaitableCodeLocationData> getWaitableCodeLocations() {
        return waitableCodeLocationData;
    }

    public Set<String> getNonWaitableCodeLocations() {
        return nonWaitableCodeLocations;
    }
}
