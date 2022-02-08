package com.synopsys.integration.detect.docs.model;

import java.util.List;

import com.synopsys.integration.detect.docs.copied.HelpJsonOption;

public class SplitGroup {
    private final String groupName;
    private final String superGroup;
    private final String location;
    private final List<HelpJsonOption> simple;
    private final List<HelpJsonOption> advanced;
    private final List<HelpJsonOption> deprecated;

    public SplitGroup(String groupName, String superGroup, String location, List<HelpJsonOption> simple, List<HelpJsonOption> advanced, List<HelpJsonOption> deprecated) {
        this.groupName = groupName;
        this.superGroup = superGroup;
        this.location = location;
        this.simple = simple;
        this.advanced = advanced;
        this.deprecated = deprecated;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getSuperGroup() {
        return superGroup;
    }

    public String getLocation() {
        return location;
    }

    public List<HelpJsonOption> getSimple() {
        return simple;
    }

    public List<HelpJsonOption> getAdvanced() {
        return advanced;
    }

    public List<HelpJsonOption> getDeprecated() {
        return deprecated;
    }
}
