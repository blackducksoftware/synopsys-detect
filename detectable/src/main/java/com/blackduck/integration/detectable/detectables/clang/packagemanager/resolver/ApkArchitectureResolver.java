package com.blackduck.integration.detectable.detectables.clang.packagemanager.resolver;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.blackduck.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.blackduck.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;
import com.blackduck.integration.executable.ExecutableOutput;
import com.blackduck.integration.executable.ExecutableRunnerException;

public class ApkArchitectureResolver {
    private Optional<String> architecture = Optional.empty();
    private boolean hasAttemptedResolution = false;

    public Optional<String> resolveArchitecture(ClangPackageManagerInfo currentPackageManager, File workingDirectory, DetectableExecutableRunner executableRunner)
        throws ExecutableRunnerException {
        if (hasAttemptedResolution) {
            return architecture;
        }

        hasAttemptedResolution = true;
        if (currentPackageManager.getPkgArchitectureArgs().isPresent()) {
            List<String> args = currentPackageManager.getPkgArchitectureArgs().get();
            String cmd = currentPackageManager.getPkgMgrCmdString();
            ExecutableOutput architectureOutput = executableRunner.execute(workingDirectory, cmd, args);
            architecture = Optional.ofNullable(architectureOutput.getStandardOutput().trim());
        } else {
            architecture = Optional.empty();
        }
        return architecture;
    }
}
