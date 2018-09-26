package com.blackducksoftware.integration.hub.detect.bomtool.nuget.inspector;

import java.io.File;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public interface NugetInspector {

    ExecutableOutput execute(ExecutableRunner executableRunner, File workingDirectory, List<String> arguments) throws ExecutableRunnerException;

}
