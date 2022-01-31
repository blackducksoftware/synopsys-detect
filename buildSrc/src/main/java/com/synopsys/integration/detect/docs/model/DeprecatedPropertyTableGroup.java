package com.synopsys.integration.detect.docs.model;

import java.util.List;

import com.synopsys.integration.detect.docs.copied.HelpJsonOption;

public class DeprecatedPropertyTableGroup {
    private final String groupName;
    private final String location;
    private final List<HelpJsonOption> options;

    public DeprecatedPropertyTableGroup(String groupName, String location, List<HelpJsonOption> options) {
        this.groupName = groupName;
        this.location = location;
        this.options = options;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getLocation() {
        return location;
    }

    public List<HelpJsonOption> getOptions() {
        return options;
    }
}
