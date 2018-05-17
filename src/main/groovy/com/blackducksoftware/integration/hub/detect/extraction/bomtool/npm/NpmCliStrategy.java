package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.extraction.result.ExecutableNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class NpmCliStrategy extends Strategy<NpmCliContext, NpmCliExtractor>{
    public static final String NODE_MODULES= "node_modules";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public NpmExecutableFinder npmExecutableFinder;

    public NpmCliStrategy() {
        super("Npm Cli", BomToolType.NPM, NpmCliContext.class, NpmCliExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final NpmCliContext context) {
        final File pom= fileFinder.findFile(environment.getDirectory(), NODE_MODULES);
        if (pom == null) {
            return new FileNotFoundStrategyResult(NODE_MODULES);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final NpmCliContext context) throws StrategyException {
        context.npmExe = npmExecutableFinder.findNpm(environment);

        if (context.npmExe == null) {
            return new ExecutableNotFoundStrategyResult("npm");
        }

        return new PassedStrategyResult();
    }

}
