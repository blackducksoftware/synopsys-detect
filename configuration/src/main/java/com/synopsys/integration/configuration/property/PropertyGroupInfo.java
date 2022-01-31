package com.synopsys.integration.configuration.property;

import java.util.Arrays;
import java.util.List;

import com.synopsys.integration.configuration.util.Group;

//data class PropertyGroupInfo(val primaryGroup:Group,val additionalGroups:List<Group>)
public class PropertyGroupInfo {
    private final Group primaryGroup;
    private final List<Group> additionalGroups;

    public PropertyGroupInfo(Group primaryGroup, Group[] additionalGroups) {

        this.primaryGroup = primaryGroup;
        this.additionalGroups = Arrays.asList(additionalGroups);
    }

    public Group getPrimaryGroup() {
        return primaryGroup;
    }

    public List<Group> getAdditionalGroups() {
        return additionalGroups;
    }
}
