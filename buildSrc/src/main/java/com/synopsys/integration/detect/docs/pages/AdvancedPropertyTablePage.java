package com.synopsys.integration.detect.docs.pages;

import java.util.List;

import com.synopsys.integration.detect.docs.model.SplitGroup;

public class AdvancedPropertyTablePage {
    private final List<SplitGroup> groups;

    public AdvancedPropertyTablePage(List<SplitGroup> groups) {
        this.groups = groups;
    }

    public List<SplitGroup> getGroups() {
        return groups;
    }
}
