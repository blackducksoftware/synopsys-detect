package com.blackduck.integration.detectable.detectables.rebar.model;

import java.util.Optional;

import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.blackduck.integration.util.NameVersion;

public class RebarParseResult {
    private final Optional<NameVersion> projectNameVersion;
    private final CodeLocation codeLocation;

    public RebarParseResult(NameVersion projectNameVersion, CodeLocation codeLocation) {
        this.projectNameVersion = Optional.of(projectNameVersion);
        this.codeLocation = codeLocation;
    }

    public RebarParseResult(CodeLocation codeLocation) {
        this.projectNameVersion = Optional.empty();
        this.codeLocation = codeLocation;
    }

    public Optional<NameVersion> getProjectNameVersion() {
        return projectNameVersion;
    }

    public CodeLocation getCodeLocation() {
        return codeLocation;
    }
}
