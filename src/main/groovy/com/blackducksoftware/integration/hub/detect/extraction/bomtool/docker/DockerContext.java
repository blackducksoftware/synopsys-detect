package com.blackducksoftware.integration.hub.detect.extraction.bomtool.docker;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;

public class DockerContext extends ExtractionContext {
    public File directory;

    public File bashExe;
    public File dockerExe;

    public String image;
    public String tar;

    public DockerInspectorInfo dockerInspectorInfo;
}
