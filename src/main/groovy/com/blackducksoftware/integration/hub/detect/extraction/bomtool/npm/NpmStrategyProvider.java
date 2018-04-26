package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import java.util.Arrays;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;

public class NpmStrategyProvider extends StrategyProvider {

    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";
    public static final String PACKAGE_LOCK_JSON = "package-lock.json";
    public static final String SHRINKWRAP_JSON = "npm-shrinkwrap.json";

    @SuppressWarnings("rawtypes")
    @Override
    public List<Strategy> createStrategies() {

        final Strategy cliStrategy = newStrategyBuilder(NpmCliContext.class, NpmCliExtractor.class)
                .requiresFile(PACKAGE_JSON).then((file, context) -> context.packageJson = file)
                .requiresFile(NODE_MODULES).then((file, context) -> context.nodeModules = file)
                .demandsExecutable(ExecutableType.NPM).then((file, context) -> context.npmExe = file)
                .build();


        return Arrays.asList(cliStrategy);

    }

}
