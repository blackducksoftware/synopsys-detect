package com.synopsys.integration.detectable.detectable.inspector.nuget;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface NugetInspectorResolver {
    NugetInspector resolveNugetInspector() throws DetectableException;
}
