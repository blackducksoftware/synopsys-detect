package com.synopsys.integration.detect.tool.sigma;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.Executable;

public class SigmaScanOperation {
    private final DirectoryManager directoryManager;
    private final DetectExecutableRunner executableRunner;

    public SigmaScanOperation(DirectoryManager directoryManager, DetectExecutableRunner executableRunner) {
        this.directoryManager = directoryManager;
        this.executableRunner = executableRunner;
    }

    public File performSigmaScan(File scanTarget, File sigmaExe, @Nullable String additionalArguments, int count) throws IntegrationException {
        String resultsFileName = String.format("results-%s.json", scanTarget.getName());
        File outputDir = new File(directoryManager.getSigmaOutputDirectory(), "SIGMA-" + count);
        outputDir.mkdirs();
        File resultsFile = new File(outputDir, resultsFileName);

        List<String> sigmaArgs = new LinkedList<>();
        sigmaArgs.add("analyze");
        Optional.ofNullable(additionalArguments)
            .map(arg -> arg.split(" "))
            .ifPresent(args -> sigmaArgs.addAll(Arrays.asList(args)));
        sigmaArgs.add("-o");
        sigmaArgs.add(resultsFile.getAbsolutePath());
        sigmaArgs.add(scanTarget.getAbsolutePath());

        Executable executable = ExecutableUtils.createFromTarget(scanTarget, ExecutableTarget.forFile(sigmaExe), sigmaArgs);
        try {
            executableRunner.executeSuccessfully(executable);
            return resultsFile;
        } catch (ExecutableFailedException e) {
            throw new IntegrationException(String.format(
                "Sigma scan command %s failed with code %d: %s",
                executable.getExecutableDescription(),
                e.getReturnCode(),
                e.getMessage()
            ));
        }
    }
}
