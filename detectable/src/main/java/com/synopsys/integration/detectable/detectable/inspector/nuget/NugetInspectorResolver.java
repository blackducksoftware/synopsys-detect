package com.synopsys.integration.detectable.detectable.inspector.nuget;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface NugetInspectorResolver {
    ExecutableTarget resolveNugetInspector() throws DetectableException;
}
