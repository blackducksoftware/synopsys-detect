package com.blackducksoftware.integration.hub.detect.bomtool.cran;

import java.io.File;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class CranApplicableResult extends BomToolApplicableResult {
    private final List<File> packratLockFiles;

    public CranApplicableResult(final File directory, final List<File> packratLockFiles) {
        super(directory, BomToolType.CRAN);
        this.packratLockFiles = packratLockFiles;
    }

    public List<File> getPackratLockFiles() {
        return packratLockFiles;
    }

}
