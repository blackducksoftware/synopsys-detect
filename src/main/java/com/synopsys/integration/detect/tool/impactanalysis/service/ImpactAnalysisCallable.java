/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.impactanalysis.service;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.http.BlackDuckRequestBuilder;
import com.synopsys.integration.blackduck.http.BlackDuckRequestFactory;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.util.NameVersion;

public class ImpactAnalysisCallable implements Callable<ImpactAnalysisOutput> {
    private final Gson gson;
    private final BlackDuckApiClient blackDuckService;
    private final ImpactAnalysis impactAnalysis;
    private final NameVersion projectAndVersion;
    private final String codeLocationName;
    private final BlackDuckRequestFactory requestFactory;

    public ImpactAnalysisCallable(Gson gson, BlackDuckApiClient blackDuckService, ImpactAnalysis impactAnalysis, BlackDuckRequestFactory requestFactory) {
        this.gson = gson;
        this.blackDuckService = blackDuckService;
        this.impactAnalysis = impactAnalysis;
        this.projectAndVersion = impactAnalysis.getProjectAndVersion();
        this.codeLocationName = impactAnalysis.getCodeLocationName();
        this.requestFactory = requestFactory;
    }

    @Override
    public ImpactAnalysisOutput call() {
        try {
            BlackDuckRequestBuilder requestBuilder = createRequestBuilder(impactAnalysis.getImpactAnalysisPath());
            try (Response response = blackDuckService.execute(ImpactAnalysisUploadService.IMPACT_ANALYSIS_PATH, requestBuilder)) {
                return ImpactAnalysisOutput.FROM_RESPONSE(gson, projectAndVersion, codeLocationName, response);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Failed to impact analysis file: %s because %s", impactAnalysis.getImpactAnalysisPath().toAbsolutePath(), e.getMessage());
            return ImpactAnalysisOutput.FAILURE(projectAndVersion, codeLocationName, errorMessage, e);
        }
    }

    public BlackDuckRequestBuilder createRequestBuilder(Path reportPath) {
        Map<String, File> fileMap = new HashMap<>();
        fileMap.put("file", reportPath.toFile());
        return requestFactory.createCommonPostRequestBuilder(fileMap, new HashMap<>());
    }

}
