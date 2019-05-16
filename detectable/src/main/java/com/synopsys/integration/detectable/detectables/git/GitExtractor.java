package com.synopsys.integration.detectable.detectables.git;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectables.git.model.GitConfigElement;
import com.synopsys.integration.detectable.detectables.git.parse.GitFileParser;
import com.synopsys.integration.exception.IntegrationException;

public class GitExtractor {
    private final GitFileParser gitFileParser;

    public GitExtractor(final GitFileParser gitFileParser) {
        this.gitFileParser = gitFileParser;
    }

    public final Extraction extract(final File gitConfigFile, final File gitHeadFile) {
        Extraction extraction;

        try (final InputStream gitConfigInputStream = new FileInputStream(gitConfigFile); final InputStream headFileInputStream = new FileInputStream(gitHeadFile)) {
            final String gitHead = gitFileParser.parseGitHead(headFileInputStream);
            final List<GitConfigElement> gitConfigElements = gitFileParser.parseGitConfig(gitConfigInputStream);

            final Optional<GitConfigElement> currentBranch = gitConfigElements.stream()
                                                                 .filter(gitConfigElement -> gitConfigElement.getElementType().equals("branch"))
                                                                 .filter(gitConfigElement -> gitConfigElement.containsKey("merge"))
                                                                 .filter(gitConfigElement -> gitConfigElement.getProperty("merge").equalsIgnoreCase(gitHead))
                                                                 .filter(gitConfigElement -> gitConfigElement.containsKey("remote"))
                                                                 .findFirst();

            final Optional<String> currentBranchRemoteName = currentBranch
                                                                 .map(gitConfigElement -> gitConfigElement.getProperty("remote"));

            if (!currentBranchRemoteName.isPresent()) {
                throw new IntegrationException(String.format("Failed to find a remote name for head %s", gitHead));
            }

            final Optional<String> remoteUrlOptional = gitConfigElements.stream()
                                                           .filter(gitConfigElement -> gitConfigElement.getElementType().equals("remote"))
                                                           .filter(gitConfigElement -> gitConfigElement.getName().isPresent())
                                                           .filter(gitConfigElement -> gitConfigElement.getName().get().equals(currentBranchRemoteName.get()))
                                                           .filter(gitConfigElement -> gitConfigElement.containsKey("url"))
                                                           .map(gitConfigElement -> gitConfigElement.getProperty("url"))
                                                           .findAny();

            if (!remoteUrlOptional.isPresent()) {
                throw new IntegrationException("Failed to find a remote url.");
            }

            final URL remoteURL = new URL(remoteUrlOptional.get());
            final String path = remoteURL.getPath();
            final String projectName = StringUtils.removeEnd(StringUtils.removeStart(path, "/"), ".git");
            final String projectVersionName = currentBranch.get().getName().orElse(null);

            extraction = new Extraction.Builder()
                             .success()
                             .projectName(projectName)
                             .projectVersion(projectVersionName)
                             .build();
        } catch (final IOException | IntegrationException e) {
            extraction = new Extraction.Builder().exception(e).failure("Failed to parse git config.").build();
        }

        return extraction;
    }
}
