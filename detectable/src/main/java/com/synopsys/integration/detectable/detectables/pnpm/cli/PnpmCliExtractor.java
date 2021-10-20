package com.synopsys.integration.detectable.detectables.pnpm.cli;

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
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.ExecutableOutput;

public class PnpmCliExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectableExecutableRunner executableRunner;
    private final PnpmCliParser pnpmCliParser;
    private final Gson gson;

    public PnpmCliExtractor(DetectableExecutableRunner executableRunner, PnpmCliParser pnpmCliParser, Gson gson) {
        this.executableRunner = executableRunner;
        this.pnpmCliParser = pnpmCliParser;
        this.gson = gson;
    }

    public Extraction extract(File directory, ExecutableTarget pnpmExe, @Nullable String pnpmArguments, boolean includeDevDependencies, File packageJsonFile) {
        PackageJson packageJson;
        try {
            packageJson = parsePackageJson(packageJsonFile);
        } catch (IOException e) {
            return new Extraction.Builder().exception(e).build();
        }

        //TODO- is --prod essentially --no-dev? if so, then we can have detectable just pass args in to extractor, make this a shared extractor
        List<String> pnpmListArgs = new ArrayList<>();
        pnpmListArgs.add("ls");
        pnpmListArgs.add("--json");

        Optional.ofNullable(pnpmArguments)
            .map(arg -> arg.split(" "))
            .ifPresent(additionalArguments -> pnpmListArgs.addAll(Arrays.asList(additionalArguments)));

        ExecutableOutput pnpmLsOutput;
        try {
            pnpmLsOutput = executableRunner.execute(ExecutableUtils.createFromTarget(directory, pnpmExe, pnpmListArgs));
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
        String standardOutput = pnpmLsOutput.getStandardOutput();
        String errorOutput = pnpmLsOutput.getErrorOutput();
        if (StringUtils.isNotBlank(errorOutput)) {
            logger.error("Error when running pnpm ls --json command");
            logger.error(errorOutput);
            return new Extraction.Builder().failure("Pnpm wrote to stderr while running pnpm ls.").build();
        } else if (StringUtils.isNotBlank(standardOutput)) {
            logger.debug("Parsing pnpm ls output.");
            logger.debug(standardOutput);
            NpmParseResult result = pnpmCliParser.generateCodeLocation(standardOutput, includeDevDependencies);
            String projectName = result.getProjectName() != null ? result.getProjectName() : packageJson.name;
            String projectVersion = result.getProjectVersion() != null ? result.getProjectVersion() : packageJson.version;
            return new Extraction.Builder().success(result.getCodeLocation()).projectName(projectName).projectVersion(projectVersion).build();
        } else {
            logger.error("Nothing returned from pnpm ls -json command");
            return new Extraction.Builder().failure("Pnpm returned error after running pnpm ls.").build();
        }
    }

    private PackageJson parsePackageJson(File packageJson) throws IOException {
        String packageJsonText = FileUtils.readFileToString(packageJson, StandardCharsets.UTF_8);
        return gson.fromJson(packageJsonText, PackageJson.class);
    }
}
