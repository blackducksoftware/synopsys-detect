/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.git.cli;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.GitResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "various", forge = "N/A", requirementsMarkdown = "Directory: .git. <br /><br /> Executable: git.")
public class GitCliDetectable extends Detectable {
    private static final String GIT_DIRECTORY_NAME = ".git";

    private final FileFinder fileFinder;
    private final GitCliExtractor gitCliExtractor;
    private final GitResolver gitResolver;

    private ExecutableTarget gitExecutable;

    public GitCliDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final GitCliExtractor gitCliExtractor, final GitResolver gitResolver) {
        super(environment);
        this.fileFinder = fileFinder;
        this.gitCliExtractor = gitCliExtractor;
        this.gitResolver = gitResolver;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requires = new Requirements(fileFinder, environment);
        requires.directory(GIT_DIRECTORY_NAME);
        return requires.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requires = new Requirements(fileFinder, environment);
        gitExecutable = requires.executable(gitResolver::resolveGit, "git");
        return requires.result();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return gitCliExtractor.extract(gitExecutable, environment.getDirectory());
    }

}
