package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cpan;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.extraction.result.ExecutableNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.StrategyResult;
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

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final CpanCliContext context) {
        final File makeFile = fileFinder.findFile(environment.getDirectory(), MAKEFILE);
        if (makeFile == null) {
            return new FileNotFoundStrategyResult(MAKEFILE);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final CpanCliContext context){
        final File cpan = standardExecutableFinder.getExecutable(StandardExecutableType.CPAN);

        if (cpan == null) {
            return new ExecutableNotFoundStrategyResult("cpan");
        }else {
            context.cpanExe = cpan;
        }

        final File cpanm = standardExecutableFinder.getExecutable(StandardExecutableType.CPANM);

        if (cpanm == null) {
            return new ExecutableNotFoundStrategyResult("cpanm");
        }else {
            context.cpanmExe = cpanm;
        }

        return new PassedStrategyResult();
    }


}
