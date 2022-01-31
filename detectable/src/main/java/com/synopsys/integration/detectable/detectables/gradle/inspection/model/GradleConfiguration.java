package com.synopsys.integration.detectable.detectables.gradle.inspection.model;

import java.util.ArrayList;
import java.util.List;

public class GradleConfiguration {
    private String name;
    private List<GradleTreeNode> children = new ArrayList<>();
    private boolean unresolved = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GradleTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<GradleTreeNode> children) {
        this.children = children;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public boolean isResolved() {
        return !isUnresolved();
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }
}
