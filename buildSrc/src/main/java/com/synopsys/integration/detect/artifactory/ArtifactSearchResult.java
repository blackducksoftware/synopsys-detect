/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.artifactory;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ArtifactSearchResult {
    @SerializedName("results")
    private final List<ArtifactSearchResultElement> results;

    public ArtifactSearchResult(final List<ArtifactSearchResultElement> results) {
        this.results = results;
    }

    public List<ArtifactSearchResultElement> getResults() {
        return results;
    }
}
