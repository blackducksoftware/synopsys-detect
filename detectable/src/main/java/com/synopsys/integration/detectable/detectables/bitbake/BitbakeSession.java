package com.synopsys.integration.detectable.detectables.bitbake;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeRecipe;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeRecipesParser;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class BitbakeSession {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectableExecutableRunner executableRunner;
    private final BitbakeRecipesParser bitbakeRecipesParser;
    private final File sourceDir;
    private final File buildEnvScript;
    private final List<String> sourceArguments;
    private final ExecutableTarget bashExecutable;
    private final ToolVersionLogger toolVersionLogger;
    private final BuildFileFinder buildFileFinder;

    public BitbakeSession(DetectableExecutableRunner executableRunner, BitbakeRecipesParser bitbakeRecipesParser,
        File sourceDir, File buildEnvScript, List<String> sourceArguments,
        ExecutableTarget bashExecutable, ToolVersionLogger toolVersionLogger, BuildFileFinder buildFileFinder) {
        this.executableRunner = executableRunner;
        this.bitbakeRecipesParser = bitbakeRecipesParser;
        this.sourceDir = sourceDir;
        this.buildEnvScript = buildEnvScript;
        this.sourceArguments = sourceArguments;
        this.bashExecutable = bashExecutable;
        this.toolVersionLogger = toolVersionLogger;
        this.buildFileFinder = buildFileFinder;
    }

    public File executeBitbakeForDependencies(File buildDir, String packageName, boolean followSymLinks, Integer searchDepth)
        throws ExecutableRunnerException, IOException, IntegrationException {

        String bitbakeCommand = "bitbake -g " + packageName;
        ExecutableOutput executableOutput = runBitbake(bitbakeCommand);
        int returnCode = executableOutput.getReturnCode();

        if (returnCode != 0) {
            throw new IntegrationException(String.format("Executing command '%s' returned a non-zero exit code %s", bitbakeCommand, returnCode));
        }

        return buildFileFinder.findTaskDependsFile(sourceDir, buildDir, followSymLinks, searchDepth);
    }

    public void logBitbakeVersion() {
        toolVersionLogger.log(() -> runBitbake("bitbake --version"));
    }

    public File determineBuildDir() {
        File fallbackBuildDir = new File(sourceDir, "build");
        File derivedBuildDir = null;
        try {
            ExecutableOutput output = runBitbake("pwd");
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

    public List<BitbakeRecipe> executeBitbakeForRecipeLayerCatalog() throws ExecutableRunnerException, IOException, IntegrationException {
        final String bitbakeCommand = "bitbake-layers show-recipes";
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
