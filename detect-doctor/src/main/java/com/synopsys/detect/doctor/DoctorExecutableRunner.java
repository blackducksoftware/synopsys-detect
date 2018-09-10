package com.synopsys.detect.doctor;

import java.util.List;
import java.util.function.Consumer;

import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class DoctorExecutableRunner extends ExecutableRunner {
    @Override
    public ExecutableOutput runExecutable(final Executable executable, final Consumer<String> standardLoggingMethod, final Consumer<String> traceLoggingMethod) throws ExecutableRunnerException {
        return new ExecutableOutput(0, "","");
    }
}