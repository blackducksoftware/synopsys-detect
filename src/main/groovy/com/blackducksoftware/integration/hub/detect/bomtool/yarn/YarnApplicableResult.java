package com.blackducksoftware.integration.hub.detect.bomtool.yarn;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class YarnApplicableResult extends BomToolApplicableResult {
    private final File yarnLock;

    public YarnApplicableResult(final File directory, final File yarnLock) {
        super(directory, BomToolType.PEAR);
        this.yarnLock = yarnLock;
    }

    public File getYarnLock() {
        return yarnLock;
    }


}