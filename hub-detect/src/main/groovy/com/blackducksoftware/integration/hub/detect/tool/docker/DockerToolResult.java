package com.blackducksoftware.integration.hub.detect.tool.docker;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.util.NameVersion;

public class DockerToolResult {
    public Optional<NameVersion> dockerProjectNameVersion;
    public List<DetectCodeLocation> dockerCodeLocations;
    public Optional<File> dockerTar;
}