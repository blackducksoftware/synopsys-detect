package com.synopsys.integration.detectable.detectables.git.cli;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.bdio2.model.GitInfo;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionMetadata;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableRunnerException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class GitCliExtractor {
    public static final ExtractionMetadata<GitInfo> EXTRACTION_METADATA_KEY = new ExtractionMetadata<>("gitInfo", GitInfo.class);

    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    private final GitUrlParser gitUrlParser;
    private final ToolVersionLogger toolVersionLogger;
    private final GitCommandRunner gitCommandRunner;

    public GitCliExtractor(GitUrlParser gitUrlParser, ToolVersionLogger toolVersionLogger, GitCommandRunner gitCommandRunner) {
        this.gitUrlParser = gitUrlParser;
        this.toolVersionLogger = toolVersionLogger;
        this.gitCommandRunner = gitCommandRunner;
    }

    public Extraction extract(ExecutableTarget gitExecutable, File directory) {
        try {
            toolVersionLogger.log(directory, gitExecutable);

            String remoteUrl = gitCommandRunner.getRepoUrl(gitExecutable, directory);
            String repoName = gitUrlParser.getRepoName(remoteUrl);

            Optional<String> branch = Optional.ofNullable(gitCommandRunner.getRepoBranch(gitExecutable, directory));
            if (branch.isPresent() && "HEAD".equals(branch.get())) {
                logger.info("HEAD is detached for this repo, using heuristics to find Git branch.");
                branch = gitCommandRunner.getRepoBranchBackup(gitExecutable, directory);
            }

            String commitHash = gitCommandRunner.getCommitHash(gitExecutable, directory);

            GitInfo gitInfo = new GitInfo(remoteUrl, commitHash, branch.orElse(null));

            return new Extraction.Builder()
                .success()
                .metaData(EXTRACTION_METADATA_KEY, gitInfo)
                .projectName(repoName)
                .projectVersion(branch.orElse(commitHash))
                .build();
        } catch (ExecutableRunnerException | IntegrationException | MalformedURLException e) {
            logger.debug("Failed to extract project info from the git executable.", e);
            return new Extraction.Builder()
                .success()
                .build();
        }
    }

}
