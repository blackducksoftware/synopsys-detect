package com.synopsys.integration.detectable.detectables.sbt;

import org.jetbrains.annotations.Nullable;

public class SbtDetectableOptions {
    private final String sbtCommandAdditionalArguments;

    public SbtDetectableOptions(@Nullable String sbtCommandAdditionalArguments) {
        this.sbtCommandAdditionalArguments = sbtCommandAdditionalArguments;
    }

    @Nullable
    public String getSbtCommandAdditionalArguments() {
        return sbtCommandAdditionalArguments;
    }
}
