/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gomod;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoGraphRelationship;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListAllData;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListModule;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoGraphParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoListParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoModWhyParser;
import com.synopsys.integration.detectable.detectables.go.gomod.process.GoModGraphGenerator;
import com.synopsys.integration.detectable.detectables.go.gomod.process.GoRelationshipManager;
import com.synopsys.integration.detectable.detectables.go.gomod.process.GoReplacementManager;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class GoModCliExtractor {
    private final GoModCommandExecutor goModCommandExecutor;
    private final GoListParser goListParser;
    private final GoGraphParser goGraphParser;
    private final GoModWhyParser goModWhyParser;
    private final GoModGraphGenerator goModGraphGenerator;
    private final ExternalIdFactory externalIdFactory;

    public GoModCliExtractor(GoModCommandExecutor goModCommandExecutor, GoListParser goListParser, GoGraphParser goGraphParser, GoModWhyParser goModWhyParser,
        GoModGraphGenerator goModGraphGenerator, ExternalIdFactory externalIdFactory) {
        this.goModCommandExecutor = goModCommandExecutor;
        this.goListParser = goListParser;
        this.goGraphParser = goGraphParser;
        this.goModWhyParser = goModWhyParser;
        this.goModGraphGenerator = goModGraphGenerator;
        this.externalIdFactory = externalIdFactory;
    }

    public Extraction extract(File directory, ExecutableTarget goExe, boolean dependencyVerificationEnabled) {
        try {
            List<GoListModule> goListModules = listModules(directory, goExe);
            List<GoListAllData> goListAllModules = goListAllModules(directory, goExe);
            List<GoGraphRelationship> goGraphRelationships = goGraphRelationships(directory, goExe);
            Set<String> moduleExclusions = moduleExclusions(directory, goExe, dependencyVerificationEnabled);

            GoRelationshipManager goRelationshipManager = new GoRelationshipManager(goGraphRelationships, moduleExclusions);
            GoReplacementManager goVersionManager = new GoReplacementManager(goListAllModules, externalIdFactory);
            List<CodeLocation> codeLocations = goListModules.stream()
                                                   .map(goListModule -> goModGraphGenerator.generateGraph(goListModule, goRelationshipManager, goVersionManager))
                                                   .collect(Collectors.toList());

            // No project info - hoping git can help with that.
            return new Extraction.Builder().success(codeLocations).build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private List<GoListModule> listModules(File directory, ExecutableTarget goExe) throws DetectableException, ExecutableRunnerException {
        List<String> listOutput = goModCommandExecutor.generateGoListOutput(directory, goExe);
        return goListParser.parseGoListModuleJsonOutput(listOutput);
    }

    private List<GoListAllData> goListAllModules(File directory, ExecutableTarget goExe) throws DetectableException, ExecutableRunnerException {
        List<String> listAllOutput = goModCommandExecutor.generateGoListUJsonOutput(directory, goExe);
        return goListParser.parseGoListAllJsonOutput(listAllOutput);
    }

    private List<GoGraphRelationship> goGraphRelationships(File directory, ExecutableTarget goExe) throws DetectableException, ExecutableRunnerException {
        List<String> modGraphOutput = goModCommandExecutor.generateGoModGraphOutput(directory, goExe);
        return goGraphParser.parseRelationshipsFromGoModGraph(modGraphOutput);
    }

    private Set<String> moduleExclusions(File directory, ExecutableTarget goExe, boolean dependencyVerificationEnabled) {
        Set<String> moduleExclusions = Collections.emptySet();
        if (dependencyVerificationEnabled) {
            List<String> modWhyOutput = goModCommandExecutor.generateGoModWhyOutput(directory, goExe);
            moduleExclusions = goModWhyParser.createModuleExclusionList(modWhyOutput);
        }
        return moduleExclusions;
    }

}
