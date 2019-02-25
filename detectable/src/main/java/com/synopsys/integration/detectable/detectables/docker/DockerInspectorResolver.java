package com.synopsys.integration.detectable.detectables.docker;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface DockerInspectorResolver {
    DockerInspectorInfo resolveDockerInspector() throws DetectableException;
}
