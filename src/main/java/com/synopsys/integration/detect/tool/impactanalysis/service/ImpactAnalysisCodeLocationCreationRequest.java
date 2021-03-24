/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.impactanalysis.service;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationRequest;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;

public class ImpactAnalysisCodeLocationCreationRequest extends CodeLocationCreationRequest<ImpactAnalysisBatchOutput> {
    private final ImpactAnalysisBatchRunner impactAnalysisBatchRunner;
    private final ImpactAnalysisBatch impactAnalysisBatch;

    public ImpactAnalysisCodeLocationCreationRequest(ImpactAnalysisBatchRunner impactAnalysisBatchRunner, ImpactAnalysisBatch impactAnalysisBatch) {
        this.impactAnalysisBatchRunner = impactAnalysisBatchRunner;
        this.impactAnalysisBatch = impactAnalysisBatch;
    }

    @Override
    public ImpactAnalysisBatchOutput executeRequest() throws BlackDuckIntegrationException {
        return impactAnalysisBatchRunner.executeUploads(impactAnalysisBatch);
    }

}
