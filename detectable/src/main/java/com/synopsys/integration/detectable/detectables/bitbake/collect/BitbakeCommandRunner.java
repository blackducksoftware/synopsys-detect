package com.synopsys.integration.detectable.detectables.bitbake.collect;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;

public class BitbakeCommandRunner {
    private final DetectableExecutableRunner executableRunner;
    private final List<String> sourceArguments;

    public BitbakeCommandRunner(DetectableExecutableRunner executableRunner, List<String> sourceArguments) {
        this.executableRunner = executableRunner;
        this.sourceArguments = sourceArguments;
    }

    public List<String> runBitbakeGraph(File directory, ExecutableTarget bashExecutable, File buildEnvScript, String packageName) throws ExecutableFailedException, IOException {
        return runBitbakeCommand(directory, bashExecutable, buildEnvScript, "bitbake -g " + packageName);
    }

    public List<String> runBitbakeVersion(File directory, ExecutableTarget bashExecutable, File buildEnvScript) throws ExecutableFailedException, IOException {
        return runBitbakeCommand(directory, bashExecutable, buildEnvScript, "bitbake --version");
    }

    public List<String> runPwdCommand(File directory, ExecutableTarget bashExecutable, File buildEnvScript) throws ExecutableFailedException, IOException {
        return runBitbakeCommand(directory, bashExecutable, buildEnvScript, "pwd");
    }

    public List<String> runBitbakeEnvironment(File directory, ExecutableTarget bashExecutable, File buildEnvScript) throws ExecutableFailedException, IOException {
        return runBitbakeCommand(directory, bashExecutable, buildEnvScript, "bitbake --environment");
    }

    public List<String> runBitbakeLayersShowRecipes(File directory, ExecutableTarget bashExecutable, File buildEnvScript) throws ExecutableFailedException, IOException {
        return runBitbakeCommand(directory, bashExecutable, buildEnvScript, "bitbake-layers show-recipes");
    }

    private List<String> runBitbakeCommand(File directory, ExecutableTarget bashExecutable, File buildEnvScript, String bitbakeCommand)
        throws IOException, ExecutableFailedException {
        StringBuilder sourceCommand = new StringBuilder("source " + buildEnvScript.getCanonicalPath());
        for (String sourceArgument : sourceArguments) {
            sourceCommand.append(" ");
            sourceCommand.append(sourceArgument);
        }
        return executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, bashExecutable, "-c", sourceCommand + "; " + bitbakeCommand))
            .getStandardOutputAsList();
    }
}
