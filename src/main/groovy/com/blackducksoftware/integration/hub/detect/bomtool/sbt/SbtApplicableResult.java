package com.blackducksoftware.integration.hub.detect.bomtool.sbt;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class SbtApplicableResult  extends BomToolApplicableResult {
    private final File buildDotSbt;

    public SbtApplicableResult(final File directory, final File buildDotSbt) {
        super(directory, BomToolType.SBT);
        this.buildDotSbt = buildDotSbt;
    }

    public File getBuildDotSbt() {
        return buildDotSbt;
    }


}
