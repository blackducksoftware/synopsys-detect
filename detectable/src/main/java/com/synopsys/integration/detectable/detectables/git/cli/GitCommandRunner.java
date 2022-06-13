package com.synopsys.integration.detectable.detectables.git.cli;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class GitCommandRunner {
    private static final String TAG_TOKEN = "tag: ";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectableExecutableRunner executableRunner;

    public GitCommandRunner(DetectableExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    public String getRepoUrl(ExecutableTarget gitExecutable, File directory) throws ExecutableRunnerException, IntegrationException, MalformedURLException {
        return runGitSingleLinesResponseSecretly(gitExecutable, directory, "config", "--get", "remote.origin.url");
    }

    public String getRepoBranch(ExecutableTarget gitExecutable, File directory) throws ExecutableRunnerException, IntegrationException {
        return runGitSingleLinesResponse(gitExecutable, directory, "rev-parse", "--abbrev-ref", "HEAD").trim();
    }

    // TODO: Move this parsing to it's own class. Only return the output. JM-06/2022
    public Optional<String> getRepoBranchBackup(ExecutableTarget gitExecutable, File directory) throws ExecutableRunnerException, IntegrationException {
        String output = runGitSingleLinesResponse(gitExecutable, directory, "log", "-n", "1", "--pretty=%d", "HEAD").trim();
        output = StringUtils.removeStart(output, "(");
        output = StringUtils.removeEnd(output, ")");
        String[] pieces = output.split(", ");

        String repoBranch;
        if (pieces.length != 2 || !pieces[1].startsWith(TAG_TOKEN)) {
            logger.debug(String.format("Unexpected output on git log. %s", output));
            repoBranch = null;
        } else {
            repoBranch = pieces[1].replace(TAG_TOKEN, "").trim();
        }

        return Optional.ofNullable(repoBranch);
    }

    public String getCommitHash(ExecutableTarget gitExecutable, File directory) throws IntegrationException, ExecutableRunnerException {
        return runGitSingleLinesResponse(gitExecutable, directory, "rev-parse", "HEAD").trim();
    }

    private String runGitSingleLinesResponse(ExecutableTarget gitExecutable, File directory, String... args) throws ExecutableRunnerException, IntegrationException {
        return verifyOutputAndGetFirstLine(executableRunner.execute(ExecutableUtils.createFromTarget(directory, gitExecutable, args)));
    }

    private String runGitSingleLinesResponseSecretly(ExecutableTarget gitExecutable, File directory, String... args) throws ExecutableRunnerException, IntegrationException {
        return verifyOutputAndGetFirstLine(executableRunner.executeSecretly(ExecutableUtils.createFromTarget(directory, gitExecutable, args)));
    }

    private String verifyOutputAndGetFirstLine(ExecutableOutput gitOutput) throws IntegrationException {
        if (gitOutput.getReturnCode() != 0) {
            throw new IntegrationException("git returned a non-zero status code.");
        }

        List<String> lines = gitOutput.getStandardOutputAsList();
        if (lines.size() != 1) {
            throw new IntegrationException("git output is of a different expected size.");
        }

        return lines.get(0).trim();
    }
}
