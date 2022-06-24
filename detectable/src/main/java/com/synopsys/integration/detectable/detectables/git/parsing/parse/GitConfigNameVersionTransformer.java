package com.synopsys.integration.detectable.detectables.git.parsing.parse;

import java.net.MalformedURLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.git.cli.GitUrlParser;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfig;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigBranch;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigRemote;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigResult;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class GitConfigNameVersionTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GitUrlParser gitUrlParser;

    public GitConfigNameVersionTransformer(GitUrlParser gitUrlParser) {
        this.gitUrlParser = gitUrlParser;
    }

    @SuppressWarnings("java:S2637") // Sonar isn't reading the @Nullable annotation on GitConfigResult
    public GitConfigResult transformToProjectInfo(GitConfig gitConfig, String gitHead) throws IntegrationException, MalformedURLException {
        Optional<GitConfigBranch> currentBranch = gitConfig.getGitConfigBranches().stream()
            .filter(it -> it.getMerge().equalsIgnoreCase(gitHead))
            .findFirst();

        String projectName;
        String projectVersionName;
        String remoteUrl;
        if (currentBranch.isPresent()) {
            logger.debug(String.format("Parsing a git repository on branch '%s'.", currentBranch.get().getName()));

            String remoteName = currentBranch.get().getRemoteName();
            remoteUrl = gitConfig.getGitConfigRemotes().stream()
                .filter(it -> it.getName().equals(remoteName))
                .map(GitConfigRemote::getUrl)
                .findFirst()
                .orElseThrow(() -> new IntegrationException(String.format("Failed to find a url for remote '%s'.", remoteName)));

            projectName = gitUrlParser.getRepoName(remoteUrl);
            projectVersionName = currentBranch.get().getName();
        } else {
            logger.debug(String.format("Parsing a git repository with detached head '%s'.", gitHead));

            remoteUrl = gitConfig.getGitConfigRemotes().stream()
                .findFirst()
                .map(GitConfigRemote::getUrl)
                .orElseThrow(() -> new IntegrationException("No remote urls were found in config."));

            projectName = gitUrlParser.getRepoName(remoteUrl);
            projectVersionName = gitHead;
        }

        NameVersion nameVersion = new NameVersion(projectName, projectVersionName);

        return new GitConfigResult(
            nameVersion,
            remoteUrl,
            currentBranch
                .map(GitConfigBranch::getName)
                .orElse(null)
        );
    }
}
