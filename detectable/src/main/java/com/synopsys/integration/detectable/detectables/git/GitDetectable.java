/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.git;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.GitResolver;
import com.synopsys.integration.detectable.detectable.explanation.FoundExecutable;
import com.synopsys.integration.detectable.detectable.explanation.FoundFile;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FailedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.git.cli.GitCliExtractor;
import com.synopsys.integration.detectable.detectables.git.parsing.GitParseExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

import java.io.File;
import java.util.Arrays;

@DetectableInfo(language = "various", forge = "N/A", requirementsMarkdown = "Directory: .git. (Executable: git OR Files: .git/config, .git/HEAD).")
public class GitDetectable extends Detectable {
    private static final String GIT_DIRECTORY_NAME = ".git";
    private static final String GIT_CONFIG_FILENAME = "config";
    private static final String GIT_HEAD_FILENAME = "HEAD";

    private final FileFinder fileFinder;
    private final GitCliExtractor gitCliExtractor;
    private final GitResolver gitResolver;
    private final GitParseExtractor gitParseExtractor;

    private ExecutableTarget gitExecutable;
    private File gitDirectory;
    private File gitConfigFile;
    private File gitHeadFile;

    private boolean canParse;

    public GitDetectable(DetectableEnvironment environment, FileFinder fileFinder, GitCliExtractor gitCliExtractor, GitResolver gitResolver, GitParseExtractor gitParseExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.gitCliExtractor = gitCliExtractor;
        this.gitResolver = gitResolver;
        this.gitParseExtractor = gitParseExtractor;
    }

    @Override
    public DetectableResult applicable() {
        gitDirectory = fileFinder.findFile(environment.getDirectory(), GIT_DIRECTORY_NAME);
        if (gitDirectory != null) {
            return new PassedDetectableResult(new FoundFile(gitDirectory));
        } else {
            return new FailedDetectableResult();
        }
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        try {
            gitExecutable = gitResolver.resolveGit();
        } catch (DetectableException e) {
            gitExecutable = null;
        }

        if (gitExecutable != null) {
            return new PassedDetectableResult(new FoundExecutable(gitExecutable));
        } else {
            // Couldn't find git executable, so we try to parse git files
            gitConfigFile = fileFinder.findFile(gitDirectory, GIT_CONFIG_FILENAME);
            gitHeadFile = fileFinder.findFile(gitDirectory, GIT_HEAD_FILENAME);
            if ((gitConfigFile != null && gitHeadFile != null)) {
                canParse = true;
                return new PassedDetectableResult(Arrays.asList(new FoundFile(gitConfigFile), new FoundFile(gitHeadFile)));
            }
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        // Try cli extraction first (preferred method, better results), if unsuccessful then try parse extraction
        if (gitExecutable != null) {
            Extraction extraction = gitCliExtractor.extract(gitExecutable, environment.getDirectory());
            if (extraction.isSuccess()) {
                return extraction;
            }
        }
        if (canParse) {
            Extraction extraction = gitParseExtractor.extract(gitConfigFile, gitHeadFile);
            if (extraction.isSuccess()) {
                return extraction;
            }
        }

        // We don't care if GitDetectable doesn't get results, it's essentially just a best-effort project name/version utility
        return new Extraction.Builder().success().build();
    }

}
