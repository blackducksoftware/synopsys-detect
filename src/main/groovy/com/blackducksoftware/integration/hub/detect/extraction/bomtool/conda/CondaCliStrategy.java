package com.blackducksoftware.integration.hub.detect.extraction.bomtool.conda;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
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
public class CondaCliStrategy extends Strategy<CondaCliContext, CondaCliExtractor> {
    public static final String ENVIRONEMNT_YML = "environment.yml";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public StandardExecutableFinder standardExecutableFinder;

    @Autowired
    public DetectConfiguration detectConfiguration;

    public CondaCliStrategy() {
        super("Conda Cli", BomToolType.COCOAPODS, CondaCliContext.class, CondaCliExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final CondaCliContext context) {
        final File ymlFile = fileFinder.findFile(environment.getDirectory(), ENVIRONEMNT_YML);
        if (ymlFile == null) {
            return new FileNotFoundStrategyResult(ENVIRONEMNT_YML);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final CondaCliContext context) throws StrategyException {
        final File conda = standardExecutableFinder.getExecutable(StandardExecutableType.CONDA);

        if (conda == null) {
            return new ExecutableNotFoundStrategyResult("conda");
        }else {
            context.condaExe = conda;
        }

        return new PassedStrategyResult();
    }


}
