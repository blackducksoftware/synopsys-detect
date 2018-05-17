package com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.strategy;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoDepContext;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoDepExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoInspectorManager;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.strategy.result.ExecutableNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.InspectorNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class GoLockStrategy extends Strategy<GoDepContext, GoDepExtractor> {
    public static final String GOPKG_LOCK_FILENAME = "Gopkg.lock";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public GoInspectorManager goInspectorManager;

    @Autowired
    public StandardExecutableFinder standardExecutableFinder;

    public GoLockStrategy() {
        super("Go Lock", BomToolType.GO_DEP, GoDepContext.class, GoDepExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final GoDepContext context) {
        final File lock = fileFinder.findFile(environment.getDirectory(), GOPKG_LOCK_FILENAME);
        if (lock == null) {
            return new FileNotFoundStrategyResult(GOPKG_LOCK_FILENAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final GoDepContext context) throws StrategyException {
        context.goExe = standardExecutableFinder.getExecutable(StandardExecutableType.GO);
        if (context.goExe == null) {
            return new ExecutableNotFoundStrategyResult("go");
        }

        context.goDepInspector = goInspectorManager.evaluate(environment);
        if (context.goDepInspector == null) {
            return new InspectorNotFoundStrategyResult("go");
        }

        return new PassedStrategyResult();
    }

}