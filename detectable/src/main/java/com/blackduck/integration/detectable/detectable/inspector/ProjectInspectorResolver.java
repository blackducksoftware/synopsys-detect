package com.blackduck.integration.detectable.detectable.inspector;

import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;

public interface ProjectInspectorResolver {
    ExecutableTarget resolveProjectInspector() throws DetectableException;
}
