package com.synopsys.integration.detect.tool.detector.impl;

import java.io.File;

import com.synopsys.integration.detectable.detectable.executable.resolver.GradleResolver;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorTemplateResolver;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;

public class DetectInspectorResolver implements NugetInspectorResolver, PipInspectorResolver, GradleInspectorTemplateResolver {
    @Override
    public File resolvePipInspector() {
        return null;
    }

    @Override
    public NugetInspector resolveNugetInspector() {
        return null;
    }

    @Override
    public File resolveGradleInspectorTemplate() {
        return null;
    }
}
