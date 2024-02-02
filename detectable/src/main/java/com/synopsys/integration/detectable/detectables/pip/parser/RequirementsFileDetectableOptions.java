package com.synopsys.integration.detectable.detectables.pip.parser;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class RequirementsFileDetectableOptions {
    private final String pipProjectName;
    private final List<Path> requirementsFilePaths;

    public RequirementsFileDetectableOptions(String pipProjectName, List<Path> requirementsFilePaths) {
        this.pipProjectName = pipProjectName;
        this.requirementsFilePaths = requirementsFilePaths;
    }

    public Optional<String> getPipProjectName() {
        return Optional.ofNullable(pipProjectName);
    }

    public List<Path> getRequirementsFilePaths() {
        return requirementsFilePaths;
    }

}
