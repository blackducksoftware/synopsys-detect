package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyManager;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;

public class NpmStrategyManager extends StrategyManager {

    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";
    public static final String PACKAGE_LOCK_JSON = "package-lock.json";
    public static final String SHRINKWRAP_JSON = "npm-shrinkwrap.json";

    public void createStrategies() {

        newStrategy()
        .requireFile(PACKAGE_JSON).as(NpmCliContext.PACKAGE_JSON_KEY)
        .requireFile(NODE_MODULES).as(NpmCliContext.NODE_MODULES_KEY)
        .requireExecutable(ExecutableType.NPM).as(NpmCliContext.NPM_EXE_KEY)
        .asContext(NpmCliContext.class)
        .withExtractor(NpmCliExtractor.class);

        newStrategy()
        .requireFile(PACKAGE_LOCK_JSON).as(NpmPackageLockContext.PACKAGELOCK_KEY)
        .asContext(NpmPackageLockContext.class)
        .withExtractor(NpmPackageLockExtractor.class);

        newStrategy()
        .requireFile(SHRINKWRAP_JSON).as(NpmShrinkwrapContext.SHRINKWRAP_KEY)
        .asContext(NpmShrinkwrapContext.class)
        .withExtractor(NpmShrinkwrapExtractor.class);

    }

}
