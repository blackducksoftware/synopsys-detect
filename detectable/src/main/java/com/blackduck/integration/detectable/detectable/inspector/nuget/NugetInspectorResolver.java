package com.blackduck.integration.detectable.detectable.inspector.nuget;

import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;

public interface NugetInspectorResolver {
    ExecutableTarget resolveNugetInspector() throws DetectableException;
}
