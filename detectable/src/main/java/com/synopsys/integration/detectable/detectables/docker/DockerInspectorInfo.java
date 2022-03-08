package com.synopsys.integration.detectable.detectables.docker;

import java.io.File;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class DockerInspectorInfo {
    private final File dockerInspectorJar;
    private final List<File> airGapInspectorImageTarFiles;

    public DockerInspectorInfo(File dockerInspectorJar) {
        this.dockerInspectorJar = dockerInspectorJar;
        this.airGapInspectorImageTarFiles = null;
    }

    public DockerInspectorInfo(File dockerInspectorJar, List<File> airGapInspectorImageTarFiles) {
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
