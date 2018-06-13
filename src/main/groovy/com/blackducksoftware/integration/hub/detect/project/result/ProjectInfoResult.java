package com.blackducksoftware.integration.hub.detect.project.result;

import java.util.Optional;

import org.slf4j.Logger;

import com.blackducksoftware.integration.util.NameVersion;

public abstract class ProjectInfoResult {
    public abstract Optional<NameVersion> getChosenNameVersion();
    public abstract boolean didChoose();
    public abstract void printDescription(final Logger logger);
}
