package com.synopsys.integration.detectable.detectables.bitbake;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeRecipe;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeEnvironmentParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeRecipesParser;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class BitbakeSession {
    private static final String BITBAKE_ENVIRONMENT_COMMAND = "bitbake --environment";
    private static final String BITBAKE_LAYERS_SHOW_RECIPES_COMMAND = "bitbake-layers show-recipes";
    public static final String BITBAKE_DEPENDENCIES_COMMAND_BASE = "bitbake -g ";
    public static final String BITBAKE_VERSION_COMMAND = "bitbake --version";
    public static final String DEFAULT_BUILD_DIR_NAME = "build";
    public static final String GET_WORKING_DIR_COMMAND = "pwd";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectableExecutableRunner executableRunner;
    private final BitbakeRecipesParser bitbakeRecipesParser;
    private final File sourceDir;
    private final File buildEnvScript;
    private final List<String> sourceArguments;
    private final ExecutableTarget bashExecutable;
    private final ToolVersionLogger toolVersionLogger;
    private final BuildFileFinder buildFileFinder;
    private final BitbakeEnvironmentParser bitbakeEnvironmentParser;

    public BitbakeSession(DetectableExecutableRunner executableRunner, BitbakeRecipesParser bitbakeRecipesParser,
        File sourceDir, File buildEnvScript, List<String> sourceArguments,
        ExecutableTarget bashExecutable, ToolVersionLogger toolVersionLogger, BuildFileFinder buildFileFinder,
        BitbakeEnvironmentParser bitbakeEnvironmentParser) {
        this.executableRunner = executableRunner;
        this.bitbakeRecipesParser = bitbakeRecipesParser;
        this.sourceDir = sourceDir;
        this.buildEnvScript = buildEnvScript;
        this.sourceArguments = sourceArguments;
        this.bashExecutable = bashExecutable;
        this.toolVersionLogger = toolVersionLogger;
        this.buildFileFinder = buildFileFinder;
        this.bitbakeEnvironmentParser = bitbakeEnvironmentParser;
    }

    public File executeBitbakeForDependencies(File buildDir, String packageName, boolean followSymLinks, Integer searchDepth)
        throws ExecutableRunnerException, IOException, IntegrationException {

        String bitbakeCommand = BITBAKE_DEPENDENCIES_COMMAND_BASE + packageName;
        ExecutableOutput executableOutput = runBitbake(bitbakeCommand);
        int returnCode = executableOutput.getReturnCode();

        if (returnCode != 0) {
            throw new IntegrationException(String.format("Executing command '%s' returned a non-zero exit code %s", bitbakeCommand, returnCode));
        }

        return buildFileFinder.findTaskDependsFile(sourceDir, buildDir, followSymLinks, searchDepth);
    }

    public void logBitbakeVersion() {
        toolVersionLogger.log(() -> runBitbake(BITBAKE_VERSION_COMMAND));
    }

    public File determineBuildDir() {
        File fallbackBuildDir = new File(sourceDir, DEFAULT_BUILD_DIR_NAME);
        File derivedBuildDir = null;
        try {
            ExecutableOutput output = runBitbake(GET_WORKING_DIR_COMMAND);
            List<String> pwdOutputLines = output.getStandardOutputAsList();
            derivedBuildDir = new File(pwdOutputLines.get(pwdOutputLines.size()-1).trim());
        } catch (Exception e) {
            logger.warn("Unable to determine build directory location due to error: {}; ; using {} for build dir", e.getMessage(), fallbackBuildDir.getAbsolutePath());
            return fallbackBuildDir;
        }
        if (derivedBuildDir.isDirectory()) {
            logger.debug("Derived build dir: {}", derivedBuildDir.getAbsolutePath());
        } else {
            logger.warn("Derived build dir {} is not a directory; using {} for build dir", derivedBuildDir.getAbsolutePath(), fallbackBuildDir.getAbsolutePath());
            return fallbackBuildDir;
        }
        return derivedBuildDir;
    }

    public Optional<String> executeBitbakeForArch() {
        String getEnvironmentBitbakeCommand = BITBAKE_ENVIRONMENT_COMMAND;
        try {
            ExecutableOutput output = runBitbake(getEnvironmentBitbakeCommand);
            List<String> envOutputLines = output.getStandardOutputAsList();
            return bitbakeEnvironmentParser.parseArchitecture(envOutputLines);
        } catch (Exception e) {
            logger.warn("Unable to determine architecture due to error executing {}: {}", getEnvironmentBitbakeCommand, e.getMessage());
            return Optional.empty();
        }
    }

    public List<BitbakeRecipe> executeBitbakeForRecipeLayerCatalog() throws ExecutableRunnerException, IOException, IntegrationException {
        final String bitbakeCommand = BITBAKE_LAYERS_SHOW_RECIPES_COMMAND;
        ExecutableOutput executableOutput = runBitbake(bitbakeCommand);
        if (executableOutput.getReturnCode() == 0) {
            return bitbakeRecipesParser.parseShowRecipes(executableOutput.getStandardOutputAsList());
        } else {
            throw new IntegrationException("Running command '%s' returned a non-zero exit code. Failed to extract bitbake recipe mapping.");
        }
    }

    private ExecutableOutput runBitbake(String bitbakeCommand) throws ExecutableRunnerException, IOException {
        StringBuilder sourceCommand = new StringBuilder("source " + buildEnvScript.getCanonicalPath());
        for (String sourceArgument : sourceArguments) {
            sourceCommand.append(" ");
            sourceCommand.append(sourceArgument);
        }
        return executableRunner.execute(ExecutableUtils.createFromTarget(sourceDir, bashExecutable, "-c", sourceCommand.toString() + "; " + bitbakeCommand));
    }
}
