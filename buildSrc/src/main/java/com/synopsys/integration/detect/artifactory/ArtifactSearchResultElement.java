/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.artifactory;

import com.google.gson.annotations.SerializedName;

public class ArtifactSearchResultElement {
    @SerializedName("downloadUri")
    private final String downloadUri;

    public ArtifactSearchResultElement(final String downloadUri) {
        this.downloadUri = downloadUri;
    }

    public String getDownloadUri() {
        return downloadUri;
    }
}
