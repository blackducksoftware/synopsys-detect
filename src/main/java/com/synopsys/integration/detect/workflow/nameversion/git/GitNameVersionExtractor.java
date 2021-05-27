/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.nameversion.git;

import java.io.File;
import java.util.Optional;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detect.workflow.nameversion.git.cli.GitCliExtractor;
import com.synopsys.integration.detect.workflow.nameversion.git.parse.GitParseExtractor;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.GitResolver;
import com.synopsys.integration.util.NameVersion;

public class GitNameVersionExtractor {
    private static final String GIT_DIRECTORY_NAME = ".git";
    private static final String GIT_CONFIG_FILENAME = "config";
    private static final String GIT_HEAD_FILENAME = "HEAD";

    private final GitCliExtractor gitCliExtractor;
    private final GitParseExtractor gitParseExtractor;
    private final GitResolver gitResolver;
    private final FileFinder fileFinder;

    public GitNameVersionExtractor(GitCliExtractor gitCliExtractor, GitParseExtractor gitParseExtractor, GitResolver gitResolver, FileFinder fileFinder) {
        this.gitCliExtractor = gitCliExtractor;
        this.gitParseExtractor = gitParseExtractor;
        this.gitResolver = gitResolver;
        this.fileFinder = fileFinder;
    }

    public Optional<NameVersion> extract(File sourceDirectory) {
        if (fileFinder.findFile(sourceDirectory, GIT_DIRECTORY_NAME) == null) {
            return Optional.empty();
        }

        Optional<NameVersion> cliNameVersion = cliExtract(sourceDirectory);
        if (cliNameVersion.isPresent()) {
            return cliNameVersion;
        }
        return parseExtract(sourceDirectory);
    }

    private Optional<NameVersion> cliExtract(File sourceDirectory) {
        try {
            ExecutableTarget gitExecutable = gitResolver.resolveGit();
            return gitCliExtractor.extract(gitExecutable, sourceDirectory);
        } catch (DetectableException e) {
            return Optional.empty();
        }
    }

    private Optional<NameVersion> parseExtract(File sourceDirectory) {
        File gitConfigFile = fileFinder.findFile(sourceDirectory, GIT_CONFIG_FILENAME);
        File gitHeadFile = fileFinder.findFile(sourceDirectory, GIT_HEAD_FILENAME);
        return gitParseExtractor.extract(gitConfigFile, gitHeadFile);
    }
}
