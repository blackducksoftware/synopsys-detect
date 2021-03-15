/**
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

public class ExternalSources {
    private final List<PodSource> sources = new ArrayList<>();

    @JsonAnySetter
    public void setDynamicProperty(final String name, final PodSource podSource) {
        podSource.setName(name);
        sources.add(podSource);
    }

    public List<PodSource> getSources() {
        return sources;
    }
}
