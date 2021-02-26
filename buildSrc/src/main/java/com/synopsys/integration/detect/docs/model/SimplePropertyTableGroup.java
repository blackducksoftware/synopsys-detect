/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.docs.model;

import java.util.List;

import com.synopsys.integration.detect.docs.copied.HelpJsonOption;

public class SimplePropertyTableGroup {
    private final String groupName;
    private final String location;
    private final List<HelpJsonOption> options;

    public SimplePropertyTableGroup(final String groupName, final String location, final List<HelpJsonOption> options) {
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
