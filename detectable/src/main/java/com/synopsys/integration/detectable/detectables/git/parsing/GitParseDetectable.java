/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.git.parsing;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "various", forge = "N/A", requirementsMarkdown = "Files: .git/config, .git/HEAD.")
public class GitParseDetectable extends Detectable {
    private static final String GIT_DIRECTORY_NAME = ".git";
    private static final String GIT_CONFIG_FILENAME = "config";
    private static final String GIT_HEAD_FILENAME = "HEAD";

    private final FileFinder fileFinder;
    private final GitParseExtractor gitParseExtractor;

    private File gitConfigFile;
    private File gitHeadFile;

    public GitParseDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final GitParseExtractor gitParseExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.gitParseExtractor = gitParseExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requires = new Requirements(fileFinder, environment);
        File gitDirectory = requires.directory(GIT_DIRECTORY_NAME);
        requires.ifCurrentlyMet(() -> {
            gitConfigFile = requires.file(gitDirectory, GIT_CONFIG_FILENAME);
            gitHeadFile = requires.file(gitDirectory, GIT_HEAD_FILENAME);
        });
        return requires.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return gitParseExtractor.extract(gitConfigFile, gitHeadFile);
    }

}
