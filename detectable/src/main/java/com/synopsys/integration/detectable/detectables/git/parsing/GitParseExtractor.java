package com.synopsys.integration.detectable.detectables.git.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigElement;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitFileParser;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitFileTransformer;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class GitParseExtractor {
    private final GitFileParser gitFileParser;
    private final GitFileTransformer gitFileTransformer;

    public GitParseExtractor(final GitFileParser gitFileParser, final GitFileTransformer gitFileTransformer) {
        this.gitFileParser = gitFileParser;
        this.gitFileTransformer = gitFileTransformer;
    }

    public final Extraction extract(final File gitConfigFile, final File gitHeadFile) {
        try (final InputStream gitConfigInputStream = new FileInputStream(gitConfigFile); final InputStream headFileInputStream = new FileInputStream(gitHeadFile)) {
            final String gitHead = gitFileParser.parseGitHead(headFileInputStream);
            final List<GitConfigElement> gitConfigElements = gitFileParser.parseGitConfig(gitConfigInputStream);

            final NameVersion projectNameVersion = gitFileTransformer.transformGitConfigElements(gitConfigElements, gitHead);

            return new Extraction.Builder()
                       .success()
                       .projectName(projectNameVersion.getName())
                       .projectVersion(projectNameVersion.getVersion())
                       .build();
        } catch (final IOException | IntegrationException e) {
            return new Extraction.Builder().exception(e).failure("Failed to parse git config.").build();
        }
    }
}
