package com.synopsys.integration.detect.docs.pages;

import java.util.List;

import com.synopsys.integration.detect.docs.model.SimplePropertyTableGroup;

public class SimplePropertyTablePage {
    public final List<SimplePropertyTableGroup> groups;

    public SimplePropertyTablePage(List<SimplePropertyTableGroup> groups) {
        this.groups = groups;
    }

    public List<SimplePropertyTableGroup> getGroups() {
        return groups;
    }
}
