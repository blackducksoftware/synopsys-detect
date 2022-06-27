package com.synopsys.integration.detectable.detectables.git.parsing;

import static com.synopsys.integration.detectable.detectables.git.cli.GitCliExtractor.EXTRACTION_METADATA_KEY;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.bdio2.model.GitInfo;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfig;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigNode;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigResult;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitConfigNameVersionTransformer;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitConfigNodeTransformer;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitFileParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class GitParseExtractor {
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    private final GitFileParser gitFileParser;
    private final GitConfigNameVersionTransformer gitConfigExtractor;
    private final GitConfigNodeTransformer gitConfigNodeTransformer;

    public GitParseExtractor(GitFileParser gitFileParser, GitConfigNameVersionTransformer gitConfigExtractor, GitConfigNodeTransformer gitConfigNodeTransformer) {
        this.gitFileParser = gitFileParser;
        this.gitConfigExtractor = gitConfigExtractor;
        this.gitConfigNodeTransformer = gitConfigNodeTransformer;
    }

    public final Extraction extract(File gitConfigFile, File gitHeadFile, File gitOriginHeadFile) {
        try {
            String headFileContent = FileUtils.readFileToString(gitHeadFile, StandardCharsets.UTF_8);
            String gitHead = gitFileParser.parseGitHead(headFileContent);

            List<String> configFileContent = FileUtils.readLines(gitConfigFile, StandardCharsets.UTF_8);
            List<GitConfigNode> gitConfigNodes = gitFileParser.parseGitConfig(configFileContent);
            GitConfig gitConfig = gitConfigNodeTransformer.createGitConfig(gitConfigNodes);

            GitConfigResult gitConfigResult = gitConfigExtractor.transformToProjectInfo(gitConfig, gitHead);

            String headCommitHash = FileUtils.readFileToString(gitOriginHeadFile, StandardCharsets.UTF_8);
            headCommitHash = StringUtils.trimToNull(headCommitHash);

            GitInfo gitInfo = new GitInfo(
                gitConfigResult.getRemoteUrl(),
                headCommitHash,
                gitConfigResult.getBranch().orElse(null)
            );

            return new Extraction.Builder()
                .success()
                .nameVersion(gitConfigResult.getNameVersion())
                .metaData(EXTRACTION_METADATA_KEY, gitInfo)
                .build();
        } catch (IOException | IntegrationException e) {
            logger.debug("Failed to extract project info from the git config.", e);
            return new Extraction.Builder()
                .success()
                .build();
        }
    }
}
