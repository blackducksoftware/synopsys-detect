/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.git.cli;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class GitCliExtractor {
    private static final String TAG_TOKEN = "tag: ";

    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    private final DetectableExecutableRunner executableRunner;
    private final GitUrlParser gitUrlParser;

    public GitCliExtractor(final DetectableExecutableRunner executableRunner, final GitUrlParser gitUrlParser) {
        this.executableRunner = executableRunner;
        this.gitUrlParser = gitUrlParser;
    }

    public Extraction extract(final ExecutableTarget gitExecutable, final File directory) {
        try {
            final String repoName = getRepoName(gitExecutable, directory);
            String branch = getRepoBranch(gitExecutable, directory);

            if ("HEAD".equals(branch)) {
                logger.info("HEAD is detached for this repo, using heuristics to find Git branch.");
                branch = getRepoBranchBackup(gitExecutable, directory)
                             .orElseGet(() -> getCommitHash(gitExecutable, directory));
            }

            return new Extraction.Builder()
                       .success()
                       .projectName(repoName)
                       .projectVersion(branch)
                       .build();
        } catch (final ExecutableRunnerException | IntegrationException | MalformedURLException e) {
            logger.debug("Failed to extract project info from the git executable.", e);
            return new Extraction.Builder()
                       .success()
                       .build();
        }
    }

    private String getRepoName(final ExecutableTarget gitExecutable, final File directory) throws ExecutableRunnerException, IntegrationException, MalformedURLException {
        final String remoteUrlString = runGitSingleLinesResponseSecretly(gitExecutable, directory, "config", "--get", "remote.origin.url");
        return gitUrlParser.getRepoName(remoteUrlString);
    }

    private String getRepoBranch(final ExecutableTarget gitExecutable, final File directory) throws ExecutableRunnerException, IntegrationException {
        return runGitSingleLinesResponse(gitExecutable, directory, "rev-parse", "--abbrev-ref", "HEAD").trim();
    }

    private Optional<String> getRepoBranchBackup(final ExecutableTarget gitExecutable, final File directory) throws ExecutableRunnerException, IntegrationException {
        String output = runGitSingleLinesResponse(gitExecutable, directory, "log", "-n", "1", "--pretty=%d", "HEAD").trim();
        output = StringUtils.removeStart(output, "(");
        output = StringUtils.removeEnd(output, ")");
        final String[] pieces = output.split(", ");

        final String repoBranch;
        if (pieces.length != 2 || !pieces[1].startsWith(TAG_TOKEN)) {
            logger.debug(String.format("Unexpected output on git log. %s", output));
            repoBranch = null;
        } else {
            repoBranch = pieces[1].replace(TAG_TOKEN, "").trim();
        }

        return Optional.ofNullable(repoBranch);
    }

    private String getCommitHash(final ExecutableTarget gitExecutable, final File directory) {
        try {
            return runGitSingleLinesResponse(gitExecutable, directory, "rev-parse", "HEAD").trim();
        } catch (final ExecutableRunnerException | IntegrationException e) {
            return "";
        }
    }

    private String runGitSingleLinesResponse(final ExecutableTarget gitExecutable, final File directory, final String... args) throws ExecutableRunnerException, IntegrationException {
        return verifyOutputAndGetFirstLine(executableRunner.execute(ExecutableUtils.createFromTarget(directory, gitExecutable, args)));
    }

    private String runGitSingleLinesResponseSecretly(final ExecutableTarget gitExecutable, final File directory, final String... args) throws ExecutableRunnerException, IntegrationException {
        return verifyOutputAndGetFirstLine(executableRunner.executeSecretly(ExecutableUtils.createFromTarget(directory, gitExecutable, args)));
    }

    private String verifyOutputAndGetFirstLine(ExecutableOutput gitOutput) throws IntegrationException {
        if (gitOutput.getReturnCode() != 0) {
            throw new IntegrationException("git returned a non-zero status code.");
        }

        final List<String> lines = gitOutput.getStandardOutputAsList();
        if (lines.size() != 1) {
            throw new IntegrationException("git output is of a different expected size.");
        }

        return lines.get(0).trim();
    }
}
