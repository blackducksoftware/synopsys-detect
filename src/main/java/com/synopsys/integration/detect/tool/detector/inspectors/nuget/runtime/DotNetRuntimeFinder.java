/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors.nuget.runtime;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableResolver;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class DotNetRuntimeFinder {
    private static final String DOTNET_LIST_RUNTIMES_COMMAND = "--list-runtimes";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectableExecutableRunner executableRunner;
    private final DetectExecutableResolver executableResolver;
    private final File workingDir;

    public DotNetRuntimeFinder(DetectableExecutableRunner executableRunner, DetectExecutableResolver executableResolver, File workingDir) {
        this.executableRunner = executableRunner;
        this.executableResolver = executableResolver;
        this.workingDir = workingDir;
    }

    public List<String> listAvailableRuntimes() throws DetectableException {
        try {
            ExecutableOutput runtimesOutput = dotnetListRuntimes();
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

    private ExecutableOutput dotnetListRuntimes() throws DetectableException, ExecutableRunnerException {
        ExecutableTarget dotnetExe = executableResolver.resolveDotNet();
        if (dotnetExe != null) {
            return executableRunner.execute(ExecutableUtils.createFromTarget(workingDir, dotnetExe, DOTNET_LIST_RUNTIMES_COMMAND));
        }
        return executableRunner.execute(workingDir, "dotnet", DOTNET_LIST_RUNTIMES_COMMAND);
    }
}
