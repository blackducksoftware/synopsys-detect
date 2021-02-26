/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.docker;

import java.io.File;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class DockerInspectorInfo {
    private final File dockerInspectorJar;
    private final List<File> airGapInspectorImageTarFiles;

    public DockerInspectorInfo(final File dockerInspectorJar) {
        this.dockerInspectorJar = dockerInspectorJar;
        this.airGapInspectorImageTarFiles = null;
    }

    public DockerInspectorInfo(final File dockerInspectorJar,
        final List<File> airGapInspectorImageTarFiles) {
        this.dockerInspectorJar = dockerInspectorJar;
        this.airGapInspectorImageTarFiles = airGapInspectorImageTarFiles;
    }

    public File getDockerInspectorJar() {
        return dockerInspectorJar;
    }

    public boolean hasAirGapImageFiles() {
        return !CollectionUtils.isEmpty(airGapInspectorImageTarFiles);
    }

    public List<File> getAirGapInspectorImageTarFiles() {
        return airGapInspectorImageTarFiles;
    }
}
