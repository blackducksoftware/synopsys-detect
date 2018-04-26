package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;

public class NpmStrategyProvider extends StrategyProvider {

    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";
    public static final String PACKAGE_LOCK_JSON = "package-lock.json";
    public static final String SHRINKWRAP_JSON = "npm-shrinkwrap.json";

    public void createStrategies() {

        newStrategy(NpmCliContext.class, NpmCliExtractor.class)
        .requireFile(PACKAGE_JSON).then((file, context) -> context.packageJson = file)
        .requireFile(NODE_MODULES).then((file, context) -> context.nodeModules = file)
        .requireExecutable(ExecutableType.NPM).then((file, context) -> context.npmExe = file);




    }

}
