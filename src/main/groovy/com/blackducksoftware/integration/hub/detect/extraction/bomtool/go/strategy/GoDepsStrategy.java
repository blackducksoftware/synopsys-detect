package com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoDepsContext;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoDepsExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.extraction.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class GoDepsStrategy extends Strategy<GoDepsContext, GoDepsExtractor> {
    public static final String GODEPS_DIRECTORYNAME = "Godeps";

    @Autowired
    public DetectFileFinder fileFinder;

    public GoDepsStrategy() {
        super("Go Deps Lock File", BomToolType.GO_GODEP, GoDepsContext.class, GoDepsExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final GoDepsContext context) {
        context.goDepsDirectory = fileFinder.findFile(environment.getDirectory(), GODEPS_DIRECTORYNAME);
        if (context.goDepsDirectory == null) {
            return new FileNotFoundStrategyResult(GODEPS_DIRECTORYNAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final GoDepsContext context){
        return new PassedStrategyResult();
    }

}