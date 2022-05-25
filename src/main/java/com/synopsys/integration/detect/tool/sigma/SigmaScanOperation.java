package com.synopsys.integration.detect.tool.sigma;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.executable.Executable;

public class SigmaScanOperation {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DirectoryManager directoryManager;
    private final DetectExecutableRunner executableRunner;

    public SigmaScanOperation(DirectoryManager directoryManager, DetectExecutableRunner executableRunner) {
        this.directoryManager = directoryManager;
        this.executableRunner = executableRunner;
    }

    public SigmaScanResult performSigmaScan(File scanTarget, File sigmaExe, @Nullable String additionalArguments) {
        String resultsFileName = String.format("results-%s.json", scanTarget.getName());
        File resultsFile = new File(directoryManager.getSigmaOutputDirectory(), resultsFileName);

        List<String> sigmaArgs = new LinkedList<>();
        sigmaArgs.add("analyze");
        Optional.ofNullable(additionalArguments)
            .map(arg -> arg.split(" ")) //TODO- is split necessary?
            .ifPresent(args -> sigmaArgs.addAll(Arrays.asList(args)));
        sigmaArgs.add("-o");
        sigmaArgs.add(resultsFile.getAbsolutePath());
        sigmaArgs.add(scanTarget.getAbsolutePath());

        //TODO- make a dir whose name includes a counter for each scan, put results file in dir

        Executable executable = ExecutableUtils.createFromTarget(scanTarget, ExecutableTarget.forFile(sigmaExe), sigmaArgs);
        try {
            executableRunner.executeSuccessfully(executable);
            return SigmaScanResult.SUCCESS(resultsFile);
        } catch (ExecutableFailedException e) {
            logger.error("Sigma scan failed with command: " + executable.getExecutableDescription());
            return SigmaScanResult.FAILURE(e.getReturnCode(), e.getMessage());
        }
    }
}
