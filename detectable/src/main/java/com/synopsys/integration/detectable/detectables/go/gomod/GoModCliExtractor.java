package com.synopsys.integration.detectable.detectables.go.gomod;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonSyntaxException;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoGraphRelationship;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListAllData;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListModule;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoGraphParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoListParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoModWhyParser;
import com.synopsys.integration.detectable.detectables.go.gomod.process.GoModDependencyManager;
import com.synopsys.integration.detectable.detectables.go.gomod.process.GoModGraphGenerator;
import com.synopsys.integration.detectable.detectables.go.gomod.process.GoRelationshipManager;
import com.synopsys.integration.detectable.extraction.Extraction;

public class GoModCliExtractor {
    private final GoModCommandExecutor goModCommandExecutor;
    private final GoListParser goListParser;
    private final GoGraphParser goGraphParser;
    private final GoModWhyParser goModWhyParser;
    private final GoModGraphGenerator goModGraphGenerator;
    private final ExternalIdFactory externalIdFactory;
    private final GoModDependencyType goModDependencyType;

    public GoModCliExtractor(
        GoModCommandExecutor goModCommandExecutor,
        GoListParser goListParser,
        GoGraphParser goGraphParser,
        GoModWhyParser goModWhyParser,
        GoModGraphGenerator goModGraphGenerator,
        ExternalIdFactory externalIdFactory,
        GoModDependencyType goModDependencyType
    ) {
        this.goModCommandExecutor = goModCommandExecutor;
        this.goListParser = goListParser;
        this.goGraphParser = goGraphParser;
        this.goModWhyParser = goModWhyParser;
        this.goModGraphGenerator = goModGraphGenerator;
        this.externalIdFactory = externalIdFactory;
        this.goModDependencyType = goModDependencyType;
    }

    public Extraction extract(File directory, ExecutableTarget goExe) throws ExecutableFailedException, JsonSyntaxException {
        List<GoListModule> goListModules = listModules(directory, goExe);
        List<GoListAllData> goListAllModules = goListAllModules(directory, goExe);
        List<GoGraphRelationship> goGraphRelationships = goGraphRelationships(directory, goExe);
        Set<String> moduleExclusions = moduleExclusions(directory, goExe);

        GoRelationshipManager goRelationshipManager = new GoRelationshipManager(goGraphRelationships, moduleExclusions);
        GoModDependencyManager goModDependencyManager = new GoModDependencyManager(goListAllModules, externalIdFactory);
        List<CodeLocation> codeLocations = goListModules.stream()
            .map(goListModule -> goModGraphGenerator.generateGraph(goListModule, goRelationshipManager, goModDependencyManager))
            .collect(Collectors.toList());

        // No project info - hoping git can help with that.
        return new Extraction.Builder().success(codeLocations).build();
    }

    private List<GoListModule> listModules(File directory, ExecutableTarget goExe) throws ExecutableFailedException, JsonSyntaxException {
        List<String> listOutput = goModCommandExecutor.generateGoListOutput(directory, goExe);
        return goListParser.parseGoListModuleJsonOutput(listOutput);
    }

    private List<GoListAllData> goListAllModules(File directory, ExecutableTarget goExe) throws ExecutableFailedException, JsonSyntaxException {
        List<String> listAllOutput = goModCommandExecutor.generateGoListJsonOutput(directory, goExe);
        return goListParser.parseGoListAllJsonOutput(listAllOutput);
    }

    private List<GoGraphRelationship> goGraphRelationships(File directory, ExecutableTarget goExe) throws ExecutableFailedException {
        List<String> modGraphOutput = goModCommandExecutor.generateGoModGraphOutput(directory, goExe);
        return goGraphParser.parseRelationshipsFromGoModGraph(modGraphOutput);
    }

    private Set<String> moduleExclusions(File directory, ExecutableTarget goExe) throws ExecutableFailedException {
        List<String> modWhyOutput = Collections.emptyList();
        if (goModDependencyType.equals(GoModDependencyType.VENDORED)) {
            modWhyOutput = goModCommandExecutor.generateGoModWhyOutput(directory, goExe, true);
        } else if (goModDependencyType.equals(GoModDependencyType.UNUSED)) {
            modWhyOutput = goModCommandExecutor.generateGoModWhyOutput(directory, goExe, false);
        }
        return goModWhyParser.createModuleExclusionList(modWhyOutput);
    }

}
