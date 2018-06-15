package com.blackducksoftware.integration.hub.detect.project;

import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.util.NameVersion;

public class BomToolProjectInfo {
    private final BomToolType bomToolType;
    private final int depth;
    private final NameVersion nameVersion;

    public BomToolProjectInfo(final BomToolType bomToolType, final int depth, final NameVersion nameVersion) {
        this.bomToolType = bomToolType;
        this.nameVersion = nameVersion;
        this.depth = depth;
    }

    public BomToolType getBomToolType() {
        return bomToolType;
    }

    public int getDepth() {
        return depth;
    }

    public NameVersion getNameVersion() {
        return nameVersion;
    }

}
