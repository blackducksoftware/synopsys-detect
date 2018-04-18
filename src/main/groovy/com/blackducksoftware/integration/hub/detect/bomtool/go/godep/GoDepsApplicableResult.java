package com.blackducksoftware.integration.hub.detect.bomtool.go.godep;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class GoDepsApplicableResult extends BomToolApplicableResult {
    private final File goDepsDirectory;

    public GoDepsApplicableResult(final File searchedDirectory, final File goDepsDirectory) {
        super(searchedDirectory, BomToolType.GO_GODEP);
        this.goDepsDirectory = goDepsDirectory;
    }

    public File getGoDepsDirectory() {
        return goDepsDirectory;
    }


}
