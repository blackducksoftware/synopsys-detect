package com.blackduck.integration.detectable.detectables.cpan;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.DetectableAccuracyType;
import com.blackduck.integration.detectable.detectable.Requirements;
import com.blackduck.integration.detectable.detectable.annotation.DetectableInfo;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.detectable.executable.resolver.CpanResolver;
import com.blackduck.integration.detectable.detectable.executable.resolver.CpanmResolver;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.executable.ExecutableRunnerException;

@DetectableInfo(name = "Cpan CLI", language = "Perl", forge = "CPAN", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: Makefile.PL. Executables: cpan, and cpanm.")
public class CpanCliDetectable extends Detectable {
    private static final String MAKEFILE = "Makefile.PL";

    private final FileFinder fileFinder;
    private final CpanResolver cpanResolver;
    private final CpanmResolver cpanmResolver;
    private final CpanCliExtractor cpanCliExtractor;

    private ExecutableTarget cpanExe;
    private ExecutableTarget cpanmExe;

    public CpanCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, CpanResolver cpanResolver, CpanmResolver cpanmResolver, CpanCliExtractor cpanCliExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.cpanResolver = cpanResolver;
        this.cpanmResolver = cpanmResolver;
        this.cpanCliExtractor = cpanCliExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(MAKEFILE);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        cpanExe = requirements.executable(cpanResolver::resolveCpan, "cpan");
        cpanmExe = requirements.executable(cpanmResolver::resolveCpanm, "cpanm");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableRunnerException {
        return cpanCliExtractor.extract(cpanExe, cpanmExe, environment.getDirectory());
    }

}

