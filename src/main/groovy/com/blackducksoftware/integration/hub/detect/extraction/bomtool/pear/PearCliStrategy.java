package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pear;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder.StandardExecutableType;
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
public class PearCliStrategy extends Strategy<PearCliContext, PearCliExtractor> {
    public static final String PACKAGE_XML_FILENAME= "package.xml";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public StandardExecutableFinder standardExecutableFinder;

    @Autowired
    public DetectConfiguration detectConfiguration;

    public PearCliStrategy() {
        super("Pear Cli", BomToolType.PEAR, PearCliContext.class, PearCliExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final PearCliContext context) {
        final File PEAR= fileFinder.findFile(environment.getDirectory(), PACKAGE_XML_FILENAME);
        if (PEAR == null) {
            return new FileNotFoundStrategyResult(PACKAGE_XML_FILENAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final PearCliContext context) throws StrategyException {
        context.pearExe = standardExecutableFinder.getExecutable(StandardExecutableType.PEAR);

        if (context.pearExe == null) {
            return new ExecutableNotFoundStrategyResult("pear");
        }

        return new PassedStrategyResult();
    }


}
