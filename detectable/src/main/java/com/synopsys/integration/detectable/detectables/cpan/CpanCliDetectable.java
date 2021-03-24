/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.cpan;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanmResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Perl", forge = "CPAN", requirementsMarkdown = "File: Makefile.PL. <br /><br /> Executable: cpan.")
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
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return cpanCliExtractor.extract(cpanExe, cpanmExe, extractionEnvironment.getOutputDirectory());
    }

}

