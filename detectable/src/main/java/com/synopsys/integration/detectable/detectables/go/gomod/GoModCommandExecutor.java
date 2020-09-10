package com.synopsys.integration.detectable.detectables.go.gomod;

import java.io.File;
import java.util.List;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;

public class GoModCommandExecutor {
    private final ExecutableRunner executableRunner;

    public GoModCommandExecutor(final ExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    public List<String> execute(File directory, File goExe, String failureMessage, String... arguments) throws DetectableException, ExecutableRunnerException {
        ExecutableOutput output = executableRunner.execute(directory, goExe, arguments);

        if (output.getReturnCode() == 0) {
            return output.getStandardOutputAsList();
        } else {
            throw new DetectableException(failureMessage + output.getReturnCode());
        }
    }
}
