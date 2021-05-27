/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.nameversion.git.parse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.nameversion.git.model.GitConfig;
import com.synopsys.integration.detect.workflow.nameversion.git.model.GitConfigNode;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.util.NameVersion;

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

    public final Optional<NameVersion> extract(File gitConfigFile, File gitHeadFile) {
        try {
            String headFileContent = FileUtils.readFileToString(gitHeadFile, StandardCharsets.UTF_8);
            String gitHead = gitFileParser.parseGitHead(headFileContent);

            List<String> configFileContent = FileUtils.readLines(gitConfigFile, StandardCharsets.UTF_8);
            List<GitConfigNode> gitConfigNodes = gitFileParser.parseGitConfig(configFileContent);
            GitConfig gitConfig = gitConfigNodeTransformer.createGitConfig(gitConfigNodes);

            return Optional.ofNullable(gitConfigExtractor.transformToProjectInfo(gitConfig, gitHead));

        } catch (IOException | IntegrationException e) {
            logger.debug("Failed to extract project info from the git config.", e);
            return Optional.empty();
        }
    }
}
