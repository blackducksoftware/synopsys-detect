/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gomod.model;

import com.synopsys.integration.util.NameVersion;

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
