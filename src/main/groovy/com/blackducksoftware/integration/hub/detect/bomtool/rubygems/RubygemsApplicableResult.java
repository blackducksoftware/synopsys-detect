package com.blackducksoftware.integration.hub.detect.bomtool.rubygems;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class RubygemsApplicableResult extends BomToolApplicableResult {
    private final File gemlock;

    public RubygemsApplicableResult(final File directory, final File gemlock) {
        super(directory, BomToolType.RUBYGEMS);
        this.gemlock = gemlock;
    }

    public File getGemLock() {
        return gemlock;
    }


}
