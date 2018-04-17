package com.blackducksoftware.integration.hub.detect.bomtool.cocoapods;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class CocoapodsApplicableResult extends BomToolApplicableResult {

    public CocoapodsApplicableResult(final File searchedDirectory, final File lockFile) {
        super(searchedDirectory, BomToolType.COCOAPODS);
        this.lockFile = lockFile;
    }

    public File lockFile;

    public File getLockFile() {
        return lockFile;
    }

}
