package com.blackducksoftware.integration.hub.detect.project.result;

import java.util.Optional;

import org.slf4j.Logger;

import com.blackducksoftware.integration.util.NameVersion;

public abstract class ProjectInfoResult {
    public abstract void printDescription(final Logger logger);

    public Optional<NameVersion> getChosenNameVersion() {
        return Optional.empty();
    }

    public boolean didChoose() {
        return getChosenNameVersion().isPresent();
    }

}
