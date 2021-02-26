/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.docker.model;

public class DockerImageInfo {
    private final String imageRepo;
    private final String imageTag;

    public DockerImageInfo(final String imageRepo, final String imageTag) {
        this.imageRepo = imageRepo;
        this.imageTag = imageTag;
    }

    public String getImageRepo() {
        return imageRepo;
    }

    public String getImageTag() {
        return imageTag;
    }
}
