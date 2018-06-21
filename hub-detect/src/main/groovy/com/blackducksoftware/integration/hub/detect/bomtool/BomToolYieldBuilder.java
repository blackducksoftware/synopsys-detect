package com.blackducksoftware.integration.hub.detect.bomtool;

public class BomToolYieldBuilder {

    private final BomToolType yieldingBomToolType;
    private BomToolType yieldingToBomToolType;

    public BomToolYieldBuilder(final BomToolType yieldingBomToolType) {
        this.yieldingBomToolType = yieldingBomToolType;
    }

    public BomToolYieldBuilder to(final BomToolType bomToolType) {
        this.yieldingToBomToolType = bomToolType;
        return this;
    }

    public BomToolType getYieldingBomToolType() {
        return yieldingBomToolType;
    }

    public BomToolType getYieldingToBomToolType() {
        return yieldingToBomToolType;
    }
}
