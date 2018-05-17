package com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoVndrContext;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoVndrExtractor;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class GoVndrStrategy extends Strategy<GoVndrContext, GoVndrExtractor> {
    public static final String VNDR_CONF_FILENAME = "vendor.conf";

    @Autowired
    public DetectFileFinder fileFinder;

    public GoVndrStrategy() {
        super("Vendor Config", BomToolType.GO_VNDR, GoVndrContext.class, GoVndrExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final GoVndrContext context) {
        context.vndrConfig = fileFinder.findFile(environment.getDirectory(), VNDR_CONF_FILENAME);
        if (context.vndrConfig == null) {
            return new FileNotFoundStrategyResult(VNDR_CONF_FILENAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final GoVndrContext context){
        return new PassedStrategyResult();
    }

}