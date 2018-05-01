package com.blackducksoftware.integration.hub.detect.extraction.bomtool.go;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.GoDepInspectorRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.StandardExecutableRequirement.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class GoStrategyProvider extends StrategyProvider {

    public static final String GOPKG_LOCK_FILENAME= "Gopkg.lock";
    public static final String GOFILE_FILENAME_PATTERN= "*.go";
    public static final String GODEPS_DIRECTORYNAME= "Godeps";
    public static final String VNDR_CONF_FILENAME= "vendor.conf";

    @Autowired
    protected DetectConfiguration detectConfiguration;

    @SuppressWarnings("rawtypes")
    @Override
    public List<Strategy> createStrategies() {

        final Strategy goDepsStrategy = newStrategyBuilder(GoDepsContext.class, GoDepsExtractor.class)
                .needsBomTool(BomToolType.GO_GODEP).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(GODEPS_DIRECTORYNAME).as((context, file) -> context.goDepsDirectory = file)
                .build();

        final Strategy goVndrStrategy = newStrategyBuilder(GoVndrContext.class, GoVndrExtractor.class)
                .needsBomTool(BomToolType.GO_VNDR).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(VNDR_CONF_FILENAME).as((context, file) -> context.vndrConfig = file)
                .build();

        final Strategy goLockStrategy = newStrategyBuilder(GoDepContext.class, GoDepExtractor.class)
                .needsBomTool(BomToolType.GO_DEP).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(GOPKG_LOCK_FILENAME).noop()
                .demandsStandardExecutable(StandardExecutableType.GO).as((context, file) -> context.goExe = file)
                .demands(new GoDepInspectorRequirement(), (context, file) -> context.goDepInspector = file)
                .build();

        final Strategy goFallbackStrategy = newStrategyBuilder(GoDepContext.class, GoDepExtractor.class)
                .needsBomTool(BomToolType.GO_DEP).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFiles(GOFILE_FILENAME_PATTERN).noop()
                .demandsStandardExecutable(StandardExecutableType.GO).as((context, file) -> context.goExe = file)
                .demands(new GoDepInspectorRequirement(), (context, file) -> context.goDepInspector = file)
                .yieldsTo(goDepsStrategy)
                .yieldsTo(goVndrStrategy)
                .yieldsTo(goLockStrategy)
                .build();



        return Arrays.asList(goDepsStrategy, goVndrStrategy, goLockStrategy, goFallbackStrategy);

    }

}
