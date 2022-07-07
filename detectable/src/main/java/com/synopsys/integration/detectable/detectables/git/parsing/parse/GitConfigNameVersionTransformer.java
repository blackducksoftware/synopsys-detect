package com.synopsys.integration.detectable.detectables.git.parsing.parse;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
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
    public GitConfigResult transformToProjectInfo(@Nullable GitConfig gitConfig, @Nullable String gitHead) throws IntegrationException, MalformedURLException {
        Optional<GitConfigBranch> currentBranch = Optional.ofNullable(gitConfig)
            .map(GitConfig::getGitConfigBranches)
            .map(Collection::stream)
            .map(branches -> branches
                .filter(branch -> branch.getMerge().equalsIgnoreCase(gitHead))
                .findFirst()
            )
            .filter(Optional::isPresent)
            .map(Optional::get);

        String projectName = null;
        String projectVersionName;
        String remoteUrl;
        if (currentBranch.isPresent()) {
            logger.debug(String.format("Parsing a git repository on branch '%s'.", currentBranch.get().getName()));

            String remoteName = currentBranch.get().getRemoteName();
            remoteUrl = Optional.of(gitConfig)
                .map(GitConfig::getGitConfigRemotes)
                .map(Collection::stream)
                .map(remotes -> remotes
                    .filter(it -> it.getName().equals(remoteName))
                    .map(GitConfigRemote::getUrl)
                    .findFirst()
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElse(null);

            if (remoteUrl != null) {
                projectName = gitUrlParser.getRepoName(remoteUrl);
            } else {
                logger.debug(String.format("Failed to find a url for remote '%s'.", remoteName));
            }
            projectVersionName = currentBranch.get().getName();
        } else {
            logger.debug(String.format("Parsing a git repository with detached head '%s'.", gitHead));

            remoteUrl = Optional.ofNullable(gitConfig)
                .map(GitConfig::getGitConfigRemotes)
                .map(Collection::stream)
                .map(Stream::findFirst)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(GitConfigRemote::getUrl)
                .orElse(null);

            projectVersionName = gitHead;
        }

        if (remoteUrl != null) {
            projectName = StringUtils.trimToNull(gitUrlParser.getRepoName(remoteUrl));
        } else {
            logger.debug("No remote urls were found in config. No project name could be inferred.");
        }
        projectVersionName = StringUtils.trimToNull(projectVersionName);

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
