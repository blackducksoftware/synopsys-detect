package com.synopsys.integration.detect.tool.detector.inspectors.nuget.runtime;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;

public class DotNetRuntimeFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ExecutableRunner executableRunner;
    private File workingDir;

    public DotNetRuntimeFinder(ExecutableRunner executableRunner, File workingDir) {
        this.executableRunner = executableRunner;
        this.workingDir = workingDir;
    }

    public List<String> listAvailableRuntimes() throws DetectableException {
        try {
            ExecutableOutput runtimesOutput = executableRunner.execute(workingDir, "dotnet", "--list-runtimes");
            List<String> foundRuntimes = runtimesOutput.getStandardOutputAsList()
                                             .stream()
                                             .map(StringUtils::trimToEmpty)
                                             .filter(StringUtils::isNotBlank)
                                             .collect(Collectors.toList());
            logger.info("Found {} available dotnet runtimes", foundRuntimes.size());
            if (foundRuntimes.isEmpty()) {
                throw new DetectableException("No available dotnet runtimes");
            }
            return foundRuntimes;
        } catch (ExecutableRunnerException e) {
            throw new DetectableException("Could not determine available dotnet runtimes", e);
        }
    }
}
