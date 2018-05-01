package com.blackducksoftware.integration.hub.detect.extraction.bomtool.docker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DockerInspectorInfo {
    public String version;

    public File dockerInspectorScript;

    public boolean isOffline = false;
    public List<File> offlineTars = new ArrayList<File>();
    public File offlineDockerInspectorJar;
}
