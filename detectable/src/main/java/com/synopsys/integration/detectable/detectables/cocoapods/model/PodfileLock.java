/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.cocoapods.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PodfileLock {
    @JsonProperty("PODS")
    private List<Pod> pods;

    @JsonProperty("DEPENDENCIES")
    private List<Pod> dependencies;

    @JsonProperty("EXTERNAL SOURCES")
    private ExternalSources externalSources;

    public List<Pod> getPods() {
        return pods;
    }

    public List<Pod> getDependencies() {
        return dependencies;
    }

    public ExternalSources getExternalSources() {
        return externalSources;
    }
}
