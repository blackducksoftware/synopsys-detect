package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cpan;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class CpanCliStrategy extends Strategy<CpanCliContext, CpanCliExtractor> {
    public static final String MAKEFILE = "Makefile.PL";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public StandardExecutableFinder standardExecutableFinder;

    @Autowired
    public DetectConfiguration detectConfiguration;

    public CpanCliStrategy() {
        super("Cpan Cli", BomToolType.CPAN, CpanCliContext.class, CpanCliExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final CpanCliContext context) {
        final File makeFile = fileFinder.findFile(evaluation.getDirectory(), MAKEFILE);
        if (makeFile == null) {
            return Applicable.doesNotApply("No makefile was found with pattern: " + MAKEFILE);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final CpanCliContext context){
        final File cpan = standardExecutableFinder.getExecutable(StandardExecutableType.CPAN);

        if (cpan == null) {
            return Extractable.canNotExtract("No Cpan executable was found.");
        }else {
            context.cpanExe = cpan;
        }

        final File cpanm = standardExecutableFinder.getExecutable(StandardExecutableType.CPANM);

        if (cpanm == null) {
            return Extractable.canNotExtract("No Cpanm executable was found.");
        }else {
            context.cpanmExe = cpanm;
        }

        return Extractable.canExtract();
    }


}
