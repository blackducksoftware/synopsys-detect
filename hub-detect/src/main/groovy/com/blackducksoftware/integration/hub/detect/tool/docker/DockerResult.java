package com.blackducksoftware.integration.hub.detect.tool.docker;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class DockerResult {

    private final Extraction extraction;
    private final File dockerTarFile;

    public DockerResult(Extraction extraction, File dockerTarFile) {
        this.extraction = extraction;
        this.dockerTarFile = dockerTarFile;
    }

    public Extraction getExtraction() {
        return extraction;
    }

    public File getDockerTarFile() {
        return dockerTarFile;
    }
}
