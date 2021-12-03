package com.synopsys.integration.detectable.detectable.inspector.nuget.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class ExeNugetInspector implements NugetInspector {
    private final String inspectorExe;
    private final DetectableExecutableRunner executableRunner;

    public ExeNugetInspector(DetectableExecutableRunner executableRunner, String inspectorExe) {
        this.executableRunner = executableRunner;
        this.inspectorExe = inspectorExe;
    }

    @Override
    public ExecutableOutput execute(File workingDirectory, File sourcePath, File outputDirectory, NugetInspectorOptions nugetInspectorOptions) throws ExecutableRunnerException, IOException {
        List<String> arguments = NugetInspectorArguments.fromInspectorOptions(nugetInspectorOptions, sourcePath, outputDirectory);
        return executableRunner.execute(workingDirectory, inspectorExe, arguments);
    }
}