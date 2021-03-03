/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.docs.pages;

import java.util.List;

import com.synopsys.integration.detect.docs.model.SplitGroup;

public class AdvancedPropertyTablePage {
    private final List<SplitGroup> groups;

    public AdvancedPropertyTablePage(final List<SplitGroup> groups) {
        this.groups = groups;
    }

    public List<SplitGroup> getGroups() {
        return groups;
    }
}
