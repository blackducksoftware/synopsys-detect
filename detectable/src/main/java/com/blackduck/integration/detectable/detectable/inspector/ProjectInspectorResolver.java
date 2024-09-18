package com.blackduck.integration.detectable.detectable.inspector;

import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.ExecutableTarget;

public interface ProjectInspectorResolver {
    ExecutableTarget resolveProjectInspector() throws DetectableException;
}
