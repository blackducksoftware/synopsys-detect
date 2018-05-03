package com.blackducksoftware.integration.hub.detect.extraction.bomtool.conda;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.StandardExecutableRequirement.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class CondaStrategyProvider extends StrategyProvider {

    public static final String ENVIRONEMNT_YML = "environment.yml";

    @SuppressWarnings("rawtypes")
    @Override
    public void init() {

        final Strategy cliStrategy = newStrategyBuilder(CondaCliContext.class, CondaCliExtractor.class)
                .needsBomTool(BomToolType.CONDA).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(ENVIRONEMNT_YML).noop()
                .demandsStandardExecutable(StandardExecutableType.CONDA).as((context, file) -> context.condaExe = file)
                .build();

        add(cliStrategy );

    }
}
