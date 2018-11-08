package com.blackducksoftware.integration.hub.detect.detector.nuget.inspector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class DotNetCoreNugetInspector implements NugetInspector {

    private String dotnetExe;
    private String inspectorDll;

    public DotNetCoreNugetInspector(String dotnetExe, String inspectorDll) {
        this.dotnetExe = dotnetExe;
        this.inspectorDll = inspectorDll;
    }

    @Override
    public ExecutableOutput execute(ExecutableRunner executableRunner, File workingDirectory, List<String> arguments) throws ExecutableRunnerException {
        List<String> dotnetArguments = new ArrayList<String>();
        dotnetArguments.add(inspectorDll);
        dotnetArguments.addAll(arguments);

        final Executable hubNugetInspectorExecutable = new Executable(workingDirectory, dotnetExe, dotnetArguments);
        final ExecutableOutput executableOutput = executableRunner.execute(hubNugetInspectorExecutable);
        return executableOutput;
    }
}