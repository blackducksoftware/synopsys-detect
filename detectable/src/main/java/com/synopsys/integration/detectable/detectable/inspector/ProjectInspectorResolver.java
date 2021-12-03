package com.synopsys.integration.detectable.detectable.inspector;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface ProjectInspectorResolver {
    ExecutableTarget resolveProjectInspector() throws DetectableException;
}
