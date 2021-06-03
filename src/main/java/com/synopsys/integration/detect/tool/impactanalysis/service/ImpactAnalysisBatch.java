/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.impactanalysis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImpactAnalysisBatch {
    private final List<ImpactAnalysis> impactAnalyses = new ArrayList<>();

    public ImpactAnalysisBatch() {
    }

    public ImpactAnalysisBatch(ImpactAnalysis... impactAnalyses) {
        this.impactAnalyses.addAll(Arrays.asList(impactAnalyses));
    }

    public void addImpactAnalysis(ImpactAnalysis impactAnalysis) {
        impactAnalyses.add(impactAnalysis);
    }

    public List<ImpactAnalysis> getImpactAnalyses() {
        return impactAnalyses;
    }

}
