package com.blackduck.integration.detectable.detectables.docker;

import com.blackduck.integration.detectable.detectable.exception.DetectableException;

public interface DockerInspectorResolver {
    DockerInspectorInfo resolveDockerInspector() throws DetectableException;
}
