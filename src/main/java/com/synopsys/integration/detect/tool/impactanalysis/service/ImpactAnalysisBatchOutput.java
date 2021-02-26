/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.impactanalysis.service;

import java.util.List;

import org.slf4j.Logger;

import com.synopsys.integration.blackduck.codelocation.CodeLocationBatchOutput;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;

public class ImpactAnalysisBatchOutput extends CodeLocationBatchOutput<ImpactAnalysisOutput> {
    public ImpactAnalysisBatchOutput(List<ImpactAnalysisOutput> outputs) {
        super(outputs);
    }

    public void throwExceptionForError(Logger logger) throws BlackDuckIntegrationException {
        for (ImpactAnalysisOutput impactAnalysisOutput : this) {
            if (impactAnalysisOutput.getStatusCode() == 404) {
                logger.error("Impact analysis upload failed with 404. Your version of Black Duck may not support Vulnerability Impact Analysis.");
            } else if (impactAnalysisOutput.getStatusCode() < 200 || impactAnalysisOutput.getStatusCode() >= 300) {
                logger.error(String.format("Unknown status code: %d", impactAnalysisOutput.getStatusCode()));
                throw new BlackDuckIntegrationException(String.format("Unknown status code when uploading impact analysis: %d, %s", impactAnalysisOutput.getStatusCode(), impactAnalysisOutput.getStatusMessage()));
            }
        }
    }

}
