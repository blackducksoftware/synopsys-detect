package com.synopsys.integration.detect.tool.signaturescanner;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detect.workflow.blackduck.codelocation.WaitableCodeLocationData;

public class SignatureScannerCodeLocationResult {
    @NotNull
    private final WaitableCodeLocationData waitableCodeLocations;
    @NotNull
    private final Set<String> nonWaitableCodeLocations;

    public SignatureScannerCodeLocationResult(
        @NotNull WaitableCodeLocationData waitableCodeLocations,
        @NotNull Set<String> nonWaitableCodeLocations
    ) {
        this.waitableCodeLocations = waitableCodeLocations;
        this.nonWaitableCodeLocations = nonWaitableCodeLocations;
    }

    public WaitableCodeLocationData getWaitableCodeLocationData() {
        return waitableCodeLocations;
    }

    public Set<String> getNonWaitableCodeLocationData() {
        return nonWaitableCodeLocations;
    }
}
