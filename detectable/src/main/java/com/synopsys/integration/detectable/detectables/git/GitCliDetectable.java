package com.synopsys.integration.detectable.detectables.git;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.GitResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectables.git.cli.GitCliExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "various", forge = "N/A", requirementsMarkdown = "Directory: .git. (Executable: git OR Files: .git/config, .git/HEAD).")
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
        Extraction extraction = gitCliExtractor.extract(gitExecutable, environment.getDirectory());
        if (extraction.isSuccess()) {
            return extraction;
        }

        // We don't care if GitDetectable doesn't get results, it's essentially just a best-effort project name/version utility
        return new Extraction.Builder().success().build(); // TODO: Let's talk about this
    }

}
