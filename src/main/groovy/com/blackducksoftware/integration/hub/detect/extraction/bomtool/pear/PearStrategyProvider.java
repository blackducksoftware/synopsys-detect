package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pear;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.StandardExecutableRequirement.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class PearStrategyProvider extends StrategyProvider {

    public static final String PACKAGE_XML_FILENAME = "package.xml";

    @SuppressWarnings("rawtypes")
    @Override
    public void init() {

        final Strategy cliStrategy = newStrategyBuilder(PearCliContext.class, PearCliExtractor.class)
                .named("Pear Cli", BomToolType.PEAR)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(PACKAGE_XML_FILENAME).noop()
                .demandsStandardExecutable(StandardExecutableType.PEAR).injectInContext((context, file) -> context.pearExe = file)
                .build();

        add(cliStrategy);

    }

}
