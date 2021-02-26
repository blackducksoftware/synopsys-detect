/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property;

import java.util.Arrays;
import java.util.List;

import com.synopsys.integration.configuration.util.Group;

//data class PropertyGroupInfo(val primaryGroup:Group,val additionalGroups:List<Group>)
public class PropertyGroupInfo {
    private final Group primaryGroup;
    private final List<Group> additionalGroups;

    public PropertyGroupInfo(final Group primaryGroup, final Group[] additionalGroups) {

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
