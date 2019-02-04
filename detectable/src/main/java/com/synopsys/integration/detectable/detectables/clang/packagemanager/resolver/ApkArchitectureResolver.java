package com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;

public class ApkArchitectureResolver {
    private Optional<String> architecture = Optional.empty();
    private boolean hasAttemptedResolution = false;

    public Optional<String> resolveArchitecture(ClangPackageManagerInfo currentPackageManager, File workingDirectory, ExecutableRunner executableRunner) throws ExecutableRunnerException {
        if (hasAttemptedResolution){
            return architecture;
        } else if (currentPackageManager.getPkgArchitectureArgs().isPresent()){
            hasAttemptedResolution = true;
            String cmd = currentPackageManager.getPkgMgrCmdString();
            List<String> args = currentPackageManager.getPkgArchitectureArgs().get();
            ExecutableOutput architectureOutput = executableRunner.execute(workingDirectory, cmd, args);
            architecture = Optional.ofNullable(architectureOutput.getStandardOutput().trim());
            return architecture;
        } else {
            hasAttemptedResolution = true;
            architecture = Optional.empty();
            return architecture;
        }
    }
}
