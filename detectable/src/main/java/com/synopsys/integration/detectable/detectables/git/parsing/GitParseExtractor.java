package com.synopsys.integration.detectable.detectables.git.parsing;

import static com.synopsys.integration.detectable.detectables.git.cli.GitCliExtractor.EXTRACTION_METADATA_KEY;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.bdio2.model.GitInfo;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfig;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigResult;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitConfigNameVersionTransformer;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitConfigNodeTransformer;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitFileParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.function.ThrowingFunction;
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

    public final Extraction extract(@Nullable File gitConfigFile, @Nullable File gitHeadFile, @Nullable File gitOriginHeadFile) {
        try {
            @Nullable
            String gitHead = Optional.ofNullable(gitHeadFile)
                .map(this::readFileToStringSafetly)
                .map(gitFileParser::parseGitHead)
                .orElse(null);

            @Nullable
            GitConfig gitConfig = Optional.ofNullable(gitConfigFile)
                .map(this::readFileToLinesSafetly)
                .map(gitFileParser::parseGitConfig)
                .map(gitConfigNodeTransformer::createGitConfig)
                .orElse(null);

            GitConfigResult gitConfigResult = gitConfigExtractor.transformToProjectInfo(gitConfig, gitHead);

            @Nullable
            String headCommitHash = StringUtils.trimToNull(readFileToStringSafetly(gitOriginHeadFile));

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
        } catch (Exception e) {
            logger.debug("Failed to extract project info from the git config.", e);
            return new Extraction.Builder()
                .success()
                .build();
        }
    }

    @Nullable
    private List<String> readFileToLinesSafetly(@Nullable File file) {
        return readFileSafetly(file, f -> FileUtils.readLines(f, StandardCharsets.UTF_8));
    }

    @Nullable
    private String readFileToStringSafetly(@Nullable File file) {
        return readFileSafetly(file, f -> FileUtils.readFileToString(f, StandardCharsets.UTF_8));
    }

    @Nullable
    private <T> T readFileSafetly(@Nullable File file, ThrowingFunction<File, T, IOException> readingFunction) {
        try {
            if (file == null) {
                // Avoid exception
                return null;
            }
            return readingFunction.apply(file);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            return null;
        }
    }
}
