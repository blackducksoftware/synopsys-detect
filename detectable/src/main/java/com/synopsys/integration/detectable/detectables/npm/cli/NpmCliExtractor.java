package com.synopsys.integration.detectable.detectables.npm.cli;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.npm.cli.parse.NpmCliParser;
import com.synopsys.integration.detectable.detectables.npm.lockfile.result.NpmPackagerResult;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.executable.ExecutableOutput;

public class NpmCliExtractor {
    public static final String OUTPUT_FILE = "detect_npm_proj_dependencies.json";
    public static final String ERROR_FILE = "detect_npm_error.json";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectableExecutableRunner executableRunner;
    private final NpmCliParser npmCliParser;
    private final Gson gson;
    private final ToolVersionLogger toolVersionLogger;

    public NpmCliExtractor(DetectableExecutableRunner executableRunner, NpmCliParser npmCliParser, Gson gson, ToolVersionLogger toolVersionLogger) {
        this.executableRunner = executableRunner;
        this.npmCliParser = npmCliParser;
        this.gson = gson;
        this.toolVersionLogger = toolVersionLogger;
    }

    public Extraction extract(File directory, ExecutableTarget npmExe, @Nullable String npmArguments, File packageJsonFile) {
        toolVersionLogger.log(directory, npmExe);
        PackageJson packageJson;
        try {
            packageJson = parsePackageJson(packageJsonFile);
        } catch (IOException e) {
            return new Extraction.Builder().exception(e).build();
        }

        List<String> exeArgs = new ArrayList<>();
        exeArgs.add("ls");
        exeArgs.add("-json");

        Optional.ofNullable(npmArguments)
            .map(arg -> arg.split(" "))
            .ifPresent(additionalArguments -> exeArgs.addAll(Arrays.asList(additionalArguments)));

        ExecutableOutput npmLsOutput;
        try {
            npmLsOutput = executableRunner.execute(ExecutableUtils.createFromTarget(directory, npmExe, exeArgs));
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
        String standardOutput = npmLsOutput.getStandardOutput();
        String errorOutput = npmLsOutput.getErrorOutput();
        if (StringUtils.isNotBlank(errorOutput)) {
            logger.error("Error when running npm ls -json command");
            logger.error(errorOutput);
            return new Extraction.Builder().failure("Npm wrote to stderr while running npm ls.").build();
        } else if (StringUtils.isNotBlank(standardOutput)) {
            logger.debug("Parsing npm ls file.");
            logger.debug(standardOutput);
            NpmPackagerResult result = npmCliParser.generateCodeLocation(standardOutput, packageJson);
            String projectName = result.getProjectName() != null ? result.getProjectName() : packageJson.name;
            String projectVersion = result.getProjectVersion() != null ? result.getProjectVersion() : packageJson.version;
            return new Extraction.Builder().success(result.getCodeLocation()).projectName(projectName).projectVersion(projectVersion).build();
        } else {
            logger.error("Nothing returned from npm ls -json command");
            return new Extraction.Builder().failure("Npm returned error after running npm ls.").build();
        }
    }

    private PackageJson parsePackageJson(File packageJson) throws IOException {
        String packageJsonText = FileUtils.readFileToString(packageJson, StandardCharsets.UTF_8);
        return gson.fromJson(packageJsonText, PackageJson.class);
    }
}
