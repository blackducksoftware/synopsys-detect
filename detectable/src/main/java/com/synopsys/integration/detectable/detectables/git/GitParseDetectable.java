package com.synopsys.integration.detectable.detectables.git;

import java.io.File;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.git.parsing.GitParseExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Git Parse", language = "various", forge = "N/A", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "Files: .git/config, .git/HEAD, .git/ORIG_HEAD.")
public class GitParseDetectable extends Detectable {
    private static final String GIT_DIRECTORY_NAME = ".git";
    private static final String GIT_CONFIG_FILENAME = "config";
    private static final String GIT_HEAD_FILENAME = "HEAD";
    private static final String GIT_ORIGIN_HEAD_FILENAME = "ORIG_HEAD";

    private final FileFinder fileFinder;
    private final GitParseExtractor gitParseExtractor;

    @Nullable
    private File gitConfigFile;
    @Nullable
    private File gitHeadFile;
    @Nullable
    private File gitOriginHeadFile;

    public GitParseDetectable(DetectableEnvironment environment, FileFinder fileFinder, GitParseExtractor gitParseExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.gitParseExtractor = gitParseExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requires = new Requirements(fileFinder, environment);
        File gitDirectory = requires.directory(GIT_DIRECTORY_NAME);
        requires.ifCurrentlyMet(() -> {
            gitConfigFile = requires.optionalFile(gitDirectory, GIT_CONFIG_FILENAME);
            gitHeadFile = requires.optionalFile(gitDirectory, GIT_HEAD_FILENAME);
            gitOriginHeadFile = requires.optionalFile(gitDirectory, GIT_ORIGIN_HEAD_FILENAME);
        });
        return requires.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return gitParseExtractor.extract(gitConfigFile, gitHeadFile, gitOriginHeadFile);
    }

}
