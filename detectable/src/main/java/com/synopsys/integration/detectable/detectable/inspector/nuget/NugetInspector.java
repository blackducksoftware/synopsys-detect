package com.synopsys.integration.detectable.detectable.inspector.nuget;

import java.io.File;
import java.io.IOException;

import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public interface NugetInspector {
    ExecutableOutput execute(File workingDirectory, File targetFile, File outputDirectory, NugetInspectorOptions nugetInspectorOptions)
        throws ExecutableRunnerException, IOException;
}