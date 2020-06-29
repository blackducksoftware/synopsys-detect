package com.synopsys.integration.detect.tool.impactanalysis.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.core.BlackDuckPath;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.model.RequestFactory;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;

public class ImpactAnalysisService {
    private static final BlackDuckPath IMPACT_ANALYSIS_PATH = new BlackDuckPath("/api/scans/vulnerability-impact");

    private final BlackDuckService blackDuckService;
    private final Gson gson;

    public ImpactAnalysisService(BlackDuckService blackDuckService, Gson gson) {
        this.blackDuckService = blackDuckService;
        this.gson = gson;
    }

    public ImpactAnalysisUploadResult uploadImpactAnalysisReport(Path reportPath) throws IntegrationException, IOException {
        String uri = blackDuckService.getUri(IMPACT_ANALYSIS_PATH);

        Map<String, File> fileMap = new HashMap<>();
        fileMap.put("file", reportPath.toFile());
        Request request = RequestFactory.createCommonPostRequestBuilder(fileMap, new HashMap<>())
                              .uri(uri)
                              .build();

        try (Response response = blackDuckService.execute(request)) {
            ImpactAnalysisSuccessResult impactAnalysisSuccessResult = null;
            ImpactAnalysisErrorResult impactAnalysisErrorResult = null;

            if (response.isStatusCodeSuccess()) {
                impactAnalysisSuccessResult = gson.fromJson(response.getContentString(), ImpactAnalysisSuccessResult.class);
            } else {
                impactAnalysisErrorResult = gson.fromJson(response.getContentString(), ImpactAnalysisErrorResult.class);
            }

            return new ImpactAnalysisUploadResult(impactAnalysisSuccessResult, impactAnalysisErrorResult);
        }
    }
}
