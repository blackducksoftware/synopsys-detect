package com.synopsys.integration.detect.tool.iac;

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

public class IacScanOperation {
    private final DirectoryManager directoryManager;
    private final DetectExecutableRunner executableRunner;

    public IacScanOperation(DirectoryManager directoryManager, DetectExecutableRunner executableRunner) {
        this.directoryManager = directoryManager;
        this.executableRunner = executableRunner;
    }

    public File performIacScan(File scanTarget, File iacScanExe, @Nullable String additionalArguments, int count) throws IntegrationException {
        String resultsFileName = String.format("results-%s.json", scanTarget.getName());
        File outputDir = new File(directoryManager.getIacScanOutputDirectory(), "IAC-" + count);
        outputDir.mkdirs();
        File resultsFile = new File(outputDir, resultsFileName);

        List<String> iacScanArgs = new LinkedList<>();
        iacScanArgs.add("analyze");
        Optional.ofNullable(additionalArguments)
            .map(arg -> arg.split(" "))
            .ifPresent(args -> iacScanArgs.addAll(Arrays.asList(args)));
        iacScanArgs.add("-o");
        iacScanArgs.add(resultsFile.getAbsolutePath());
        iacScanArgs.add(scanTarget.getAbsolutePath());

        Executable executable = ExecutableUtils.createFromTarget(scanTarget, ExecutableTarget.forFile(iacScanExe), iacScanArgs);
        try {
            executableRunner.executeSuccessfully(executable);
            return resultsFile;
        } catch (ExecutableFailedException e) {
            throw new IntegrationException(String.format(
                "Iac scan command %s failed with code %d: %s",
                executable.getExecutableDescription(),
                e.getReturnCode(),
                e.getMessage()
            ));
        }
    }
}
