/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.conan.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.conan.ConanDetectableResult;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.executable.ExecutableOutput;

public class ConanCliExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectableExecutableRunner executableRunner;
    private final ConanInfoParser conanInfoParser;
    private final ToolVersionLogger toolVersionLogger;

    public ConanCliExtractor(DetectableExecutableRunner executableRunner, ConanInfoParser conanInfoParser, ToolVersionLogger toolVersionLogger) {
        this.executableRunner = executableRunner;
        this.conanInfoParser = conanInfoParser;
        this.toolVersionLogger = toolVersionLogger;
    }

    public Extraction extract(File projectDir, ExecutableTarget conanExe, ConanCliExtractorOptions conanCliExtractorOptions) {
        toolVersionLogger.logOutputSafelyIfDebug(() -> executableRunner.execute(ExecutableUtils.createFromTarget(projectDir, conanExe, "--version")));
        List<String> exeArgs = generateConanInfoCmdArgs(projectDir, conanCliExtractorOptions);
        ExecutableOutput conanInfoOutput;
        try {
            conanInfoOutput = executableRunner.execute(ExecutableUtils.createFromTarget(projectDir, conanExe, exeArgs));
        } catch (Exception e) {
            logger.error(String.format("Exception thrown executing conan info command: %s", e.getMessage()));
            return new Extraction.Builder().exception(e).build();
        }
        if (!wasSuccess(conanInfoOutput)) {
            return new Extraction.Builder().failure("Conan info command reported errors").build();
        }
        if (!producedOutput(conanInfoOutput)) {
            return new Extraction.Builder().failure("Conan info command produced no output").build();
        }
        try {
            ConanDetectableResult result = conanInfoParser.generateCodeLocationFromConanInfoOutput(conanInfoOutput.getStandardOutput(),
                conanCliExtractorOptions.shouldIncludeDevDependencies(), conanCliExtractorOptions.preferLongFormExternalIds());
            return new Extraction.Builder().success(result.getCodeLocation()).projectName(result.getProjectName()).projectVersion(result.getProjectVersion()).build();
        } catch (DetectableException e) {
            return new Extraction.Builder().failure(e.getMessage()).build();
        }
    }

    private boolean wasSuccess(ExecutableOutput conanInfoOutput) {
        String errorOutput = conanInfoOutput.getErrorOutput();
        if (StringUtils.isNotBlank(errorOutput) && errorOutput.contains("ERROR: ")) {
            logger.error("The conan info command reported errors: {}", errorOutput);
            return false;
        }
        if (StringUtils.isNotBlank(errorOutput)) {
            logger.debug("The conan info command wrote to stderr: {}", errorOutput);
        }
        return true;
    }

    private boolean producedOutput(ExecutableOutput conanInfoOutput) {
        String standardOutput = conanInfoOutput.getStandardOutput();
        if (StringUtils.isBlank(standardOutput)) {
            logger.error("Nothing returned from conan info command");
            return false;
        }
        return true;
    }

    @NotNull
    private List<String> generateConanInfoCmdArgs(File projectDir, ConanCliExtractorOptions conanCliExtractorOptions) {
        List<String> exeArgs = new ArrayList<>();
        exeArgs.add("info");
        conanCliExtractorOptions.getLockfilePath().ifPresent(lockfilePath -> {
            exeArgs.add("--lockfile");
            exeArgs.add(lockfilePath.toString());
        });
        conanCliExtractorOptions.getAdditionalArguments().ifPresent(argsString -> {
            String[] additionalArgs = argsString.split(" +");
            exeArgs.addAll(Arrays.asList(additionalArgs));
        });
        exeArgs.add(projectDir.getAbsolutePath());
        return exeArgs;
    }
}
