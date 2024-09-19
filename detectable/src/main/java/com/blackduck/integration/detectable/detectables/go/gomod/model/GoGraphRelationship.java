package com.blackduck.integration.detectable.detectables.go.gomod.model;

import com.blackduck.integration.util.NameVersion;

public class GoGraphRelationship {
    private final NameVersion parent;
    private final NameVersion child;

    public GoGraphRelationship(NameVersion parent, NameVersion child) {
        this.parent = parent;
        this.child = child;
    }

    public NameVersion getParent() {
        return parent;
    }

    public NameVersion getChild() {
        return child;
    }
}
