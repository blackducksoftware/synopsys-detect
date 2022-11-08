package com.synopsys.integration.detectable.detectables.git;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.GitResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectables.git.cli.GitCliExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Git", language = "various", forge = "N/A", requirementsMarkdown = "Directory: .git. (Executable: git).", accuracy = DetectableAccuracyType.HIGH)
public class GitCliDetectable extends Detectable {
    private static final String GIT_DIRECTORY_NAME = ".git";

    private final FileFinder fileFinder;
    private final GitCliExtractor gitCliExtractor;
    private final GitResolver gitResolver;

    private ExecutableTarget gitExecutable;

    public GitCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, GitCliExtractor gitCliExtractor, GitResolver gitResolver) {
        super(environment);
        this.fileFinder = fileFinder;
        this.gitCliExtractor = gitCliExtractor;
        this.gitResolver = gitResolver;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.directory(GIT_DIRECTORY_NAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        gitExecutable = requirements.executable(gitResolver::resolveGit, "git");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return gitCliExtractor.extract(gitExecutable, environment.getDirectory());
    }

}
