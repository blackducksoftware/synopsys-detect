/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.cocoapods.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class Pod {
    private String name;
    private String cleanName;
    private List<String> dependencies = new ArrayList<>();

    public Pod() {
    }

    public Pod(final String name) {
        this.name = name;
    }

    @JsonAnySetter
    public void setDynamicProperty(final String name, final List<String> dependencies) {
        this.name = name;
        this.dependencies = dependencies;
    }

    public String getName() {
        return name;
    }

    public String getCleanName() {
        return cleanName;
    }

    public List<String> getDependencies() {
        return dependencies;
    }
}
