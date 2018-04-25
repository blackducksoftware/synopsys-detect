package com.blackducksoftware.integration.hub.detect.bomtool;

import com.blackducksoftware.integration.hub.detect.type.ExecutableType;

public class BomToolRequirementBuilder {

    public BomToolRequirementBuilder() {

    }

    public BomToolRequirementBuilder requireExecutable(final ExecutableType type) {
        return this;
    }

    public BomToolRequirementBuilder requireFile(final String filename) {
        return this;
    }

    public BomToolRequirementBuilder requireFiles(final String filepattern) {
        return this;
    }

}
