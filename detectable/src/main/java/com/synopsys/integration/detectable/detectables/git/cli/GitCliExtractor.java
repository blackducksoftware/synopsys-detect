/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.git.cli;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.exception.IntegrationException;

public class GitCliExtractor {
    private final ExecutableRunner executableRunner;

    public GitCliExtractor(final ExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    public Extraction extract(final File gitExecutable, final File directory) {
        try {
            final String repoName = getRepoName(gitExecutable, directory);
            final String branch = getRepoBranch(gitExecutable, directory);

            return new Extraction.Builder()
                       .success()
                       .projectName(repoName)
                       .projectVersion(branch)
                       .build();
        } catch (final ExecutableRunnerException | IntegrationException | MalformedURLException e) {
            return new Extraction.Builder()
                       .exception(e)
                       .failure("Failed to extract project info from the git executable.")
                       .build();
        }
    }

    private String getRepoName(final File gitExecutable, final File directory) throws ExecutableRunnerException, IntegrationException, MalformedURLException {
        final String remote = runGitSingleLinesResponse(gitExecutable, directory, "remote");
        final String remoteUrlString = runGitSingleLinesResponse(gitExecutable, directory, "remote", "get-url", "--push", remote);
        final URL remoteURL = new URL(remoteUrlString);
        final String remoteUrlPath = remoteURL.getPath().trim();

        return StringUtils.removeEnd(StringUtils.removeStart(remoteUrlPath, "/"), ".git");
    }

    private String getRepoBranch(final File gitExecutable, final File directory) throws ExecutableRunnerException, IntegrationException {
        return runGitSingleLinesResponse(gitExecutable, directory, "rev-parse", "--abbrev-ref", "HEAD").trim();
    }

    private String runGitSingleLinesResponse(final File gitExecutable, final File directory, final String... commands) throws ExecutableRunnerException, IntegrationException {
        final ExecutableOutput gitOutput = executableRunner.execute(directory, gitExecutable, commands);

        if (gitOutput.getReturnCode() != 0) {
            throw new IntegrationException("git returned a non-zero status code.");
        }

        final List<String> lines = gitOutput.getStandardOutputAsList();
        if (lines.size() != 1) {
            throw new IntegrationException("git output is of a different expected size.");
        }

        return lines.get(0);
    }
}
