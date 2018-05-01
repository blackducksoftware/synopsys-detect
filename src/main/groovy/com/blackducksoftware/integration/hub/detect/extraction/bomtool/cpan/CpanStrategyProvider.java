package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cpan;

import java.util.Arrays;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.StandardExecutableRequirement.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class CpanStrategyProvider extends StrategyProvider {

    public static final String MAKEFILE = "Makefile.PL";

    @SuppressWarnings("rawtypes")
    @Override
    public List<Strategy> createStrategies() {

        final Strategy cpanCliStrategy = newStrategyBuilder(CpanCliContext.class, CpanCliExtractor.class)
                .needsBomTool(BomToolType.CPAN).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(MAKEFILE).noop()
                .demandsStandardExecutable(StandardExecutableType.CPAN).as((context, file) -> context.cpanExe = file)
                .demandsStandardExecutable(StandardExecutableType.CPANM).as((context, file) -> context.cpanmExe = file)
                .build();

        return Arrays.asList(cpanCliStrategy);

    }

}
