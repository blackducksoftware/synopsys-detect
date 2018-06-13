package com.blackducksoftware.integration.hub.detect.manager;

import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.util.NameVersion;

public class BomToolProjectInfo {
    private final BomToolType bomToolType;
    private final NameVersion nameVersion;
    private final int depth;

    public BomToolProjectInfo(final BomToolType bomToolType, final int depth, final NameVersion nameVersion) {
        this.bomToolType = bomToolType;
        this.nameVersion = nameVersion;
        this.depth = depth;
    }

    public BomToolType getBomToolType() {
        return bomToolType;
    }
    public NameVersion getNameVersion() {
        return nameVersion;
    }
    public int getDepth() {
        return depth;
    }

}
