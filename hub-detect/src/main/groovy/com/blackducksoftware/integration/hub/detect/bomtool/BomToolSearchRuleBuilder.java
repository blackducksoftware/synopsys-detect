package com.blackducksoftware.integration.hub.detect.bomtool;

import java.util.ArrayList;
import java.util.List;

public class BomToolSearchRuleBuilder {
    private final BomTool bomTool;
    private int maxDepth;
    private boolean nestable;
    private final List<BomToolType> yieldsTo;

    public BomToolSearchRuleBuilder(final BomTool bomTool) {
        this.bomTool = bomTool;
        yieldsTo = new ArrayList<>();
    }

    public BomToolSearchRuleBuilder defaultNotNested() {
        maxDepth(Integer.MAX_VALUE);
        return nestable(false);
    }

    public BomToolSearchRuleBuilder defaultNested() {
        maxDepth(Integer.MAX_VALUE);
        return nestable(true);
    }

    public BomToolSearchRuleBuilder maxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public BomToolSearchRuleBuilder nestable(final boolean nestable) {
        this.nestable = nestable;
        return this;
    }

    public BomToolSearchRuleBuilder yield(final BomToolType type) {
        this.yieldsTo.add(type);
        return this;
    }

    public BomToolSearchRule build() {
        return new BomToolSearchRule(bomTool, maxDepth, nestable, yieldsTo);
    }
}
