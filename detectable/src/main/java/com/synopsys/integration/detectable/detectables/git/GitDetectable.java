/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.git;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.GitResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FailedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.git.cli.GitCliExtractor;
import com.synopsys.integration.detectable.detectables.git.parsing.GitParseExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "various", forge = "N/A", requirementsMarkdown = "Directory: .git. <br /><br /> Executable: git. <br /><br /> OR <br /><br /> Files: .git/config, .git/HEAD.")
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

    private boolean cliExtract;
    private boolean parseExtract;

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
            cliExtract = true;
        }

        gitConfigFile = fileFinder.findFile(gitDirectory, GIT_CONFIG_FILENAME);
        gitHeadFile = fileFinder.findFile(gitDirectory, GIT_HEAD_FILENAME);
        if ((gitConfigFile != null && gitHeadFile != null)) {
            parseExtract = true;
        }

        if (cliExtract || parseExtract) {
            return new PassedDetectableResult();
        } else {
            return new FailedDetectableResult();
        }
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        gitExecutable = gitResolver.resolveGit();
        cliExtract = cliExtract && gitExecutable != null && StringUtils.isNotBlank(gitExecutable.toCommand());
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        // Try cli extraction first (preferred method, better results), if unsuccessful then try parse extraction
        Extraction extraction = new Extraction.Builder().failure("Could not extract project data using Git Detector.").build();
        if (cliExtract) {
            extraction = gitCliExtractor.extract(gitExecutable, environment.getDirectory());
        }
        if ((extraction == null || extraction.getProjectName() == null || extraction.getProjectVersion() == null) && parseExtract) {
            extraction = gitParseExtractor.extract(gitConfigFile, gitHeadFile);
        }
        return extraction;
    }

}
