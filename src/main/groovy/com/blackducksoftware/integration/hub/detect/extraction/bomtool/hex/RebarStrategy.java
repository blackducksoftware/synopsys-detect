package com.blackducksoftware.integration.hub.detect.extraction.bomtool.hex;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.strategy.result.ExecutableNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class RebarStrategy extends Strategy<RebarContext, RebarExtractor> {
    public static final String REBAR_CONFIG = "rebar.config";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public StandardExecutableFinder standardExecutableFinder;

    public RebarStrategy() {
        super("Rebar Config", BomToolType.HEX, RebarContext.class, RebarExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final RebarContext context) {
        final File rebar = fileFinder.findFile(environment.getDirectory(), REBAR_CONFIG);
        if (rebar == null) {
            return new FileNotFoundStrategyResult(REBAR_CONFIG);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final RebarContext context) throws StrategyException {
        context.rebarExe = standardExecutableFinder.getExecutable(StandardExecutableType.CONDA);

        if (context.rebarExe == null) {
            return new ExecutableNotFoundStrategyResult("rebar");
        }

        return new PassedStrategyResult();
    }

}