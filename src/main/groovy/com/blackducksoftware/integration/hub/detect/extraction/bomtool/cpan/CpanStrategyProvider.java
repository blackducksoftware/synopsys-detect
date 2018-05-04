package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cpan;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.StandardExecutableRequirement.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class CpanStrategyProvider extends StrategyProvider {

    public static final String MAKEFILE = "Makefile.PL";

    @SuppressWarnings("rawtypes")
    @Override
    public void init() {

        final Strategy cpanCliStrategy = newStrategyBuilder(CpanCliContext.class, CpanCliExtractor.class)
                .named("Cpan Cli", BomToolType.CPAN)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(MAKEFILE).noop()
                .demandsStandardExecutable(StandardExecutableType.CPAN).as((context, file) -> context.cpanExe = file)
                .demandsStandardExecutable(StandardExecutableType.CPANM).as((context, file) -> context.cpanmExe = file)
                .build();

        add(cpanCliStrategy);

    }

}
