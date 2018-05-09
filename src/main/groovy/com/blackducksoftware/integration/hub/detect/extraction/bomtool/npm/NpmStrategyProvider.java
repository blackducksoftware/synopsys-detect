package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn.YarnStrategyProvider;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.NpmExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyBuilder;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class NpmStrategyProvider extends StrategyProvider {

    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";
    public static final String PACKAGE_LOCK_JSON = "package-lock.json";
    public static final String SHRINKWRAP_JSON = "npm-shrinkwrap.json";

    @Autowired
    public YarnStrategyProvider yarnStrategyProvider;

    @Override
    public void init() {

    }

    @SuppressWarnings("rawtypes")
    @Override
    public void lateInit() {

        final StrategyBuilder<NpmLockfileContext, NpmLockfileExtractor> packageLockStrategyBuilder = newStrategyBuilder(NpmLockfileContext.class, NpmLockfileExtractor.class)
                .named("Package lock", BomToolType.NPM)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(PACKAGE_LOCK_JSON).as((context, file) -> context.lockfile = file)
                .nestable();


        final StrategyBuilder<NpmLockfileContext, NpmLockfileExtractor> shrinkwrapStrategyBuilder = newStrategyBuilder(NpmLockfileContext.class, NpmLockfileExtractor.class)
                .named("Shrinkwrap", BomToolType.NPM)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(SHRINKWRAP_JSON).as((context, file) -> context.lockfile = file)
                .nestable();


        final StrategyBuilder<NpmCliContext, NpmCliExtractor> cliStrategyBuilder = newStrategyBuilder(NpmCliContext.class, NpmCliExtractor.class)
                .named("Npm Cli", BomToolType.NPM)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(PACKAGE_JSON).as((context, file) -> context.packageJson = file)
                .needsFile(NODE_MODULES).as((context, file) -> context.nodeModules = file)
                .demands(new NpmExecutableRequirement(), (context, file) -> context.npmExe = file)
                .nestable();

        for (final Strategy yarnStrategy : yarnStrategyProvider.getAllStrategies()) {
            packageLockStrategyBuilder.yieldsTo(yarnStrategy);
            shrinkwrapStrategyBuilder.yieldsTo(yarnStrategy);
            cliStrategyBuilder.yieldsTo(yarnStrategy);
        }

        final Strategy packageLockStrategy = packageLockStrategyBuilder.build();
        final Strategy shrinkwrapStrategy = shrinkwrapStrategyBuilder.build();

        cliStrategyBuilder.yieldsTo(shrinkwrapStrategy);
        cliStrategyBuilder.yieldsTo(packageLockStrategy);
        final Strategy cliStrategy = cliStrategyBuilder.build();

        add(cliStrategy, packageLockStrategy, shrinkwrapStrategy);

    }
}
