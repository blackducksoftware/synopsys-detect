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

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoGraphRelationship;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListModule;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListUJsonData;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoGraphParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoListParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoModWhyParser;
import com.synopsys.integration.detectable.detectables.go.gomod.process.GoModGraphGenerator;
import com.synopsys.integration.detectable.detectables.go.gomod.process.GoRelationshipManager;
import com.synopsys.integration.detectable.detectables.go.gomod.process.GoVersionManager;
import com.synopsys.integration.detectable.extraction.Extraction;

public class GoModCliExtractor {
    private final GoModCommandExecutor goModCommandExecutor;
    private final GoModGraphGenerator goModGraphGenerator;
    private final GoModWhyParser goModWhyParser;
    private final GoListParser goListParser;
    private final GoGraphParser goGraphParser;

    public GoModCliExtractor(GoModCommandExecutor executor, GoModGraphGenerator goModGraphGenerator, GoModWhyParser goModWhyParser, GoListParser goListParser, GoGraphParser goGraphParser) {
        this.goModCommandExecutor = executor;
        this.goModGraphGenerator = goModGraphGenerator;
        this.goModWhyParser = goModWhyParser;
        this.goListParser = goListParser;
        this.goGraphParser = goGraphParser;
    }

    public Extraction extract(File directory, ExecutableTarget goExe, boolean dependencyVerificationEnabled) {
        try {
            List<String> listOutput = goModCommandExecutor.generateGoListOutput(directory, goExe);
            List<GoListModule> goListModules = goListParser.parseGoListModuleJsonOutput(listOutput);

            List<String> listUJsonOutput = goModCommandExecutor.generateGoListUJsonOutput(directory, goExe);
            List<GoListUJsonData> goListUJsonData = goListParser.parseGoListUJsonOutput(listUJsonOutput);

            List<String> modGraphOutput = goModCommandExecutor.generateGoModGraphOutput(directory, goExe);
            List<GoGraphRelationship> goGraphRelationships = goGraphParser.parseRelationshipsFromGoModGraph(modGraphOutput);

            Set<String> moduleExclusionList = Collections.emptySet();
            if (dependencyVerificationEnabled) {
                List<String> modWhyOutput = goModCommandExecutor.generateGoModWhyOutput(directory, goExe);
                moduleExclusionList = goModWhyParser.createModuleExclusionList(modWhyOutput);
            }

            // TODO: Move above to it's own class

            GoRelationshipManager goRelationshipManager = new GoRelationshipManager(goGraphRelationships, moduleExclusionList);
            GoVersionManager goVersionManager = new GoVersionManager(goListUJsonData);
            List<CodeLocation> codeLocations = goListModules.stream()
                                                   .map(goListModule -> goModGraphGenerator.generateGraph(goListModule, goRelationshipManager, goVersionManager))
                                                   .collect(Collectors.toList());

            // No project info - hoping git can help with that.
            return new Extraction.Builder().success(codeLocations).build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
