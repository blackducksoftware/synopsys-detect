package com.blackducksoftware.integration.hub.detect.extraction.bomtool.go;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.GoDepInspectorRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.StandardExecutableRequirement.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class GoStrategyProvider extends StrategyProvider {

    public static final String GOPKG_LOCK_FILENAME= "Gopkg.lock";
    public static final String GOFILE_FILENAME_PATTERN= "*.go";
    public static final String GODEPS_DIRECTORYNAME= "Godeps";
    public static final String VNDR_CONF_FILENAME= "vendor.conf";

    @Autowired
    protected DetectConfiguration detectConfiguration;

    @SuppressWarnings("rawtypes")
    @Override
    public void init() {

        final Strategy goDepsStrategy = newStrategyBuilder(GoDepsContext.class, GoDepsExtractor.class)
                .named("Go Deps", BomToolType.GO_GODEP)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(GODEPS_DIRECTORYNAME).as((context, file) -> context.goDepsDirectory = file)
                .build();

        final Strategy goVndrStrategy = newStrategyBuilder(GoVndrContext.class, GoVndrExtractor.class)
                .named("Vendor Config", BomToolType.GO_VNDR)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(VNDR_CONF_FILENAME).as((context, file) -> context.vndrConfig = file)
                .build();

        final Strategy goLockStrategy = newStrategyBuilder(GoDepContext.class, GoDepExtractor.class)
                .named("Go Lock", BomToolType.GO_DEP)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(GOPKG_LOCK_FILENAME).noop()
                .demandsStandardExecutable(StandardExecutableType.GO).as((context, file) -> context.goExe = file)
                .demands(new GoDepInspectorRequirement(), (context, file) -> context.goDepInspector = file)
                .build();

        final Strategy goFallbackStrategy = newStrategyBuilder(GoDepContext.class, GoDepExtractor.class)
                .named("Go Cli", BomToolType.GO_DEP)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFiles(GOFILE_FILENAME_PATTERN).noop()
                .demandsStandardExecutable(StandardExecutableType.GO).as((context, file) -> context.goExe = file)
                .demands(new GoDepInspectorRequirement(), (context, file) -> context.goDepInspector = file)
                .yieldsTo(goDepsStrategy)
                .yieldsTo(goVndrStrategy)
                .yieldsTo(goLockStrategy)
                .build();



        add(goDepsStrategy, goVndrStrategy, goLockStrategy, goFallbackStrategy);

    }

}
