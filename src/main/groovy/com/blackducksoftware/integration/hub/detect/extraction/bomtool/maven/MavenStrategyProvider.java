package com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.MavenExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class MavenStrategyProvider extends StrategyProvider {

    public static final String POM_FILENAME = "pom.xml";
    public static final String POM_WRAPPER_FILENAME = "pom.groovy";

    @SuppressWarnings("rawtypes")
    @Override
    public void init() {

        final Strategy pomStrategy = newStrategyBuilder(MavenCliContext.class, MavenCliExtractor.class)
                .needsBomTool(BomToolType.MAVEN).noop()
                .needsFile(POM_FILENAME).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .demands(new MavenExecutableRequirement(), (context, file) -> context.mavenExe = file)
                .build();

        final Strategy pomWrapperStrategy = newStrategyBuilder(MavenCliContext.class, MavenCliExtractor.class)
                .needsBomTool(BomToolType.MAVEN).noop()
                .needsFile(POM_WRAPPER_FILENAME).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .build();

        add(pomStrategy, pomWrapperStrategy);

    }

}
