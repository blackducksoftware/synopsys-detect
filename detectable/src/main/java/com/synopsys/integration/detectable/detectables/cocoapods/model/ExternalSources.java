package com.synopsys.integration.detectable.detectables.cocoapods.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class ExternalSources {
    private final List<PodSource> sources = new ArrayList<>();

    @JsonAnySetter
    public void setDynamicProperty(String name, PodSource podSource) {
        podSource.setName(name);
        sources.add(podSource);
    }

    public List<PodSource> getSources() {
        return sources;
    }
}
