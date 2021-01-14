/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectables.git.parsing;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfig;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigNode;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitConfigNameVersionTransformer;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitConfigNodeTransformer;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitFileParser;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.util.NameVersion;

public class GitParseExtractor {
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    private final GitFileParser gitFileParser;
    private final GitConfigNameVersionTransformer gitConfigExtractor;
    private final GitConfigNodeTransformer gitConfigNodeTransformer;

    public GitParseExtractor(final GitFileParser gitFileParser, final GitConfigNameVersionTransformer gitConfigExtractor, final GitConfigNodeTransformer gitConfigNodeTransformer) {
        this.gitFileParser = gitFileParser;
        this.gitConfigExtractor = gitConfigExtractor;
        this.gitConfigNodeTransformer = gitConfigNodeTransformer;
    }

    public final Extraction extract(final File gitConfigFile, final File gitHeadFile) {
        try {
            final String headFileContent = FileUtils.readFileToString(gitHeadFile, StandardCharsets.UTF_8);
            final String gitHead = gitFileParser.parseGitHead(headFileContent);

            final List<String> configFileContent = FileUtils.readLines(gitConfigFile, StandardCharsets.UTF_8);
            final List<GitConfigNode> gitConfigNodes = gitFileParser.parseGitConfig(configFileContent);
            final GitConfig gitConfig = gitConfigNodeTransformer.createGitConfig(gitConfigNodes);

            final NameVersion projectNameVersion = gitConfigExtractor.transformToProjectInfo(gitConfig, gitHead);

            return new Extraction.Builder()
                       .success()
                       .projectName(projectNameVersion.getName())
                       .projectVersion(projectNameVersion.getVersion())
                       .build();
        } catch (final IOException | IntegrationException e) {
            logger.debug("Failed to extract project info from the git config.", e);
            return new Extraction.Builder()
                       .success()
                       .build();
        }
    }
}
