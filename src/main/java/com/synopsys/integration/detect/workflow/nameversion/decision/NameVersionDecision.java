package com.synopsys.integration.detect.workflow.nameversion.decision;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.synopsys.integration.util.NameVersion;

public abstract class NameVersionDecision {
    @Nullable
    private final NameVersion nameVersion;

    protected NameVersionDecision() {
        this(null);
    }

    protected NameVersionDecision(@Nullable NameVersion chosenNameVersion) {
        this.nameVersion = chosenNameVersion;
    }

    public Optional<NameVersion> getNameVersion() {
        return Optional.ofNullable(nameVersion);
    }

    public Optional<NameVersion> getChosenNameVersion() {
        return getNameVersion();
    }

    public abstract void printDescription(Logger logger);
}
