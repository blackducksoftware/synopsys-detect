package com.blackducksoftware.integration.hub.detect.detector.nuget.inspector;

import java.io.File;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class ExeNugetInspector implements NugetInspector {

    private String inspectorExe;

    public ExeNugetInspector(String inspectorExe) {
        this.inspectorExe = inspectorExe;
    }

    @Override
    public ExecutableOutput execute(ExecutableRunner executableRunner, File workingDirectory, List<String> arguments) throws ExecutableRunnerException {
        final Executable hubNugetInspectorExecutable = new Executable(workingDirectory, inspectorExe, arguments);
        final ExecutableOutput executableOutput = executableRunner.execute(hubNugetInspectorExecutable);
        return executableOutput;
    }
}