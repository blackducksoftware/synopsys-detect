package com.synopsys.integration.detect.docs.pages;

import java.util.List;

import com.synopsys.integration.detect.docs.model.DeprecatedPropertyTableGroup;

public class DeprecatedPropertyTablePage {
    private final List<DeprecatedPropertyTableGroup> groups;

    public DeprecatedPropertyTablePage(List<DeprecatedPropertyTableGroup> groups) {
        this.groups = groups;
    }

    public List<DeprecatedPropertyTableGroup> getGroups() {
        return groups;
    }
}
