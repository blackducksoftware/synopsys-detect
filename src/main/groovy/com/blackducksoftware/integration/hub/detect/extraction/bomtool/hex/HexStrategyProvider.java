package com.blackducksoftware.integration.hub.detect.extraction.bomtool.hex;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.StandardExecutableRequirement.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class HexStrategyProvider extends StrategyProvider {

    static final String REBAR_CONFIG = "rebar.config";

    @SuppressWarnings("rawtypes")
    @Override
    public List<Strategy> createStrategies() {

        final Strategy cpanCliStrategy = newStrategyBuilder(RebarContext.class, RebarExtractor.class)
                .needsBomTool(BomToolType.HEX).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(REBAR_CONFIG).noop()
                .demandsStandardExecutable(StandardExecutableType.REBAR3).as((context, file) -> context.rebarExe = file)
                .build();

        return Arrays.asList(cpanCliStrategy);

    }

}
