package com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.result.ExecutableNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class MavenPomWrapperStrategy extends Strategy<MavenCliContext, MavenCliExtractor> {
    public static final String POM_FILENAME= "pom.xml";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public MavenExecutableFinder mavenExecutableFinder;

    public MavenPomWrapperStrategy() {
        super("Pom file", BomToolType.MAVEN, MavenCliContext.class, MavenCliExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final MavenCliContext context) {
        final File pom= fileFinder.findFile(environment.getDirectory(), POM_FILENAME);
        if (pom == null) {
            return new FileNotFoundStrategyResult(POM_FILENAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final MavenCliContext context){
        context.mavenExe = mavenExecutableFinder.findMaven(environment);

        if (context.mavenExe == null) {
            return new ExecutableNotFoundStrategyResult("mvn");
        }

        return new PassedStrategyResult();
    }


}
