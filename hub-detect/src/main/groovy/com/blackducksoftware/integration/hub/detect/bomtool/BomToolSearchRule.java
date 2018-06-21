package com.blackducksoftware.integration.hub.detect.bomtool;

import java.util.List;

public class BomToolSearchRule {
    private final BomTool bomTool;
    private final int maxDepth;
    private final boolean nestable;
    private final List<BomToolType> yieldsTo;

    public BomToolSearchRule(final BomTool bomTool, final int maxDepth, final boolean nestable, final List<BomToolType> yieldsTo) {
        this.bomTool = bomTool;
        this.maxDepth = maxDepth;
        this.nestable = nestable;
        this.yieldsTo = yieldsTo;
    }

    public BomTool getBomTool() {
        return bomTool;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public boolean isNestable() {
        return nestable;
    }

    public List<BomToolType> getYieldsTo() {
        return yieldsTo;
    }
}
