package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.NpmExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class NpmStrategyProvider extends StrategyProvider {

    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";
    public static final String PACKAGE_LOCK_JSON = "package-lock.json";
    public static final String SHRINKWRAP_JSON = "npm-shrinkwrap.json";

    @SuppressWarnings("rawtypes")
    @Override
    public void init() {

        final Strategy packageLockStrategy = newStrategyBuilder(NpmLockfileContext.class, NpmLockfileExtractor.class)
                .needsBomTool(BomToolType.NPM).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(PACKAGE_LOCK_JSON).as((context, file) -> context.lockfile = file)
                .build();

        final Strategy shrinkwrapStrategy = newStrategyBuilder(NpmLockfileContext.class, NpmLockfileExtractor.class)
                .needsBomTool(BomToolType.NPM).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(SHRINKWRAP_JSON).as((context, file) -> context.lockfile = file)
                .build();

        final Strategy cliStrategy = newStrategyBuilder(NpmCliContext.class, NpmCliExtractor.class)
                .needsBomTool(BomToolType.NPM).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(PACKAGE_JSON).as((context, file) -> context.packageJson = file)
                .needsFile(NODE_MODULES).as((context, file) -> context.nodeModules = file)
                .demands(new NpmExecutableRequirement(), (context, file) -> context.npmExe = file)
                .yieldsTo(shrinkwrapStrategy)
                .yieldsTo(packageLockStrategy)
                .build();

        add(cliStrategy, packageLockStrategy, shrinkwrapStrategy);

    }

}
