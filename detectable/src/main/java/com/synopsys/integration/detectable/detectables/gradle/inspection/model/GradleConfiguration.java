/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.gradle.inspection.model;

import java.util.ArrayList;
import java.util.List;

public class GradleConfiguration {
    private String name;
    private List<GradleTreeNode> children = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<GradleTreeNode> getChildren() {
        return children;
    }

    public void setChildren(final List<GradleTreeNode> children) {
        this.children = children;
    }
}
