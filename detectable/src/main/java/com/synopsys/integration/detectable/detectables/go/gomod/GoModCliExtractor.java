/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gomod;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.go.gomod.process.GoModGraphGenerator;
import com.synopsys.integration.detectable.detectables.go.gomod.process.GoRelationshipManager;
import com.synopsys.integration.detectable.detectables.go.gomod.process.GoVersionManager;
import com.synopsys.integration.detectable.extraction.Extraction;

public class GoModCliExtractor {
    private final GoModDataGatherer goModDataGatherer;
    private final GoModGraphGenerator goModGraphGenerator;

    public GoModCliExtractor(GoModDataGatherer goModDataGatherer, GoModGraphGenerator goModGraphGenerator) {
        this.goModDataGatherer = goModDataGatherer;
        this.goModGraphGenerator = goModGraphGenerator;
    }

    public Extraction extract(File directory, ExecutableTarget goExe, boolean dependencyVerificationEnabled) {
        try {
            GoModDataGatherer.GoModData goModData = goModDataGatherer.gatherGoModData(directory, goExe, dependencyVerificationEnabled);

            GoRelationshipManager goRelationshipManager = new GoRelationshipManager(goModData.goGraphRelationships, goModData.moduleExclusions);
            GoVersionManager goVersionManager = new GoVersionManager(goModData.goListAllData);
            List<CodeLocation> codeLocations = goModData.goListModules.stream()
                                                   .map(goListModule -> goModGraphGenerator.generateGraph(goListModule, goRelationshipManager, goVersionManager))
                                                   .collect(Collectors.toList());

            // No project info - hoping git can help with that.
            return new Extraction.Builder().success(codeLocations).build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
