package com.synopsys.integration.detectable.detectables.conan.cli;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectables.conan.ConanDetectableResult;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.conan1.ConanInfoParser;
import com.synopsys.integration.detectable.detectables.conan.cli.process.ConanCommandRunner;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class ConanCliExtractor {
    private static final String UNABLE_TO_PARSE_VERSION_EXCEPTION_MESSAGE = "Unable to parse Conan version command output";
    private static final String CONAN_VERSION_COMMAND_OUTPUT_PREFIX = "Conan version ";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConanCommandRunner conanCommandRunner;
    private final ConanInfoParser conanInfoParser;
    private final ToolVersionLogger toolVersionLogger;

    public ConanCliExtractor(ConanCommandRunner conanCommandRunner, ConanInfoParser conanInfoParser, ToolVersionLogger toolVersionLogger) {
        this.conanCommandRunner = conanCommandRunner;
        this.conanInfoParser = conanInfoParser;
        this.toolVersionLogger = toolVersionLogger;
    }

    public Extraction extractFromConan1(File projectDir, ExecutableTarget conanExe) {
        toolVersionLogger.log(projectDir, conanExe);

        ExecutableOutput conanInfoOutput;
        try {
            conanInfoOutput = conanCommandRunner.runConanInfoCommand(projectDir, conanExe);
        } catch (Exception e) {
            logger.error(String.format("Exception thrown executing conan info command: %s", e.getMessage()));
            return new Extraction.Builder().exception(e).build();
        }
        if (!conanCommandRunner.wasSuccess(conanInfoOutput)) {
            return new Extraction.Builder().failure("Conan info command reported errors").build();
        }
        if (!conanCommandRunner.producedOutput(conanInfoOutput)) {
            return new Extraction.Builder().failure("Conan info command produced no output").build();
        }

        try {
            ConanDetectableResult result = conanInfoParser.generateCodeLocationFromConanInfoOutput(conanInfoOutput.getStandardOutput());
            return new Extraction.Builder().success(result.getCodeLocation()).projectName(result.getProjectName()).projectVersion(result.getProjectVersion()).build();
        } catch (DetectableException e) {
            return new Extraction.Builder().failure(e.getMessage()).build();
        }
    }

    public String extractGraphInfoFromConan2(File projectDir, ExecutableTarget conanExe) throws ExecutableFailedException {
        toolVersionLogger.log(projectDir, conanExe);

        return conanCommandRunner.runConanGraphInfoCommand(projectDir, conanExe).getStandardOutput();
    }

    public String extractConanMajorVersion(File projectDir, ExecutableTarget conanExe) throws ExecutableRunnerException {
        ExecutableOutput conanVersionOutput = conanCommandRunner.runConanVersionCommand(projectDir, conanExe);
        int prefixLength = CONAN_VERSION_COMMAND_OUTPUT_PREFIX.length();
        String trimmedOutput = conanVersionOutput.getStandardOutput().trim();

        if (!trimmedOutput.startsWith(CONAN_VERSION_COMMAND_OUTPUT_PREFIX)) {
            throw new RuntimeException(UNABLE_TO_PARSE_VERSION_EXCEPTION_MESSAGE);
        }
        String fullVersion = trimmedOutput.substring(prefixLength);

        int firstDotIndex = fullVersion.indexOf('.');
        if (firstDotIndex == -1) {
            throw new RuntimeException(UNABLE_TO_PARSE_VERSION_EXCEPTION_MESSAGE);
        }

        return fullVersion.substring(0, firstDotIndex);
    }
}
