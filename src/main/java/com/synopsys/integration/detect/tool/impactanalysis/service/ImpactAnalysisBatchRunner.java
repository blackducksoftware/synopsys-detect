package com.synopsys.integration.detect.tool.impactanalysis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.log.IntLogger;

public class ImpactAnalysisBatchRunner {
    private final IntLogger logger;
    private final BlackDuckApiClient blackDuckApiClient;
    private final ApiDiscovery apiDiscovery;
    private final ExecutorService executorService;
    private final Gson gson;

    public ImpactAnalysisBatchRunner(IntLogger logger, BlackDuckApiClient blackDuckApiClient, ApiDiscovery apiDiscovery, ExecutorService executorService, Gson gson) {
        this.logger = logger;
        this.blackDuckApiClient = blackDuckApiClient;
        this.apiDiscovery = apiDiscovery;
        this.executorService = executorService;
        this.gson = gson;
    }

    public ImpactAnalysisBatchOutput executeUploads(ImpactAnalysisBatch impactAnalysisBatch) throws BlackDuckIntegrationException {
        logger.info("Starting the impact analysis file uploads.");
        ImpactAnalysisBatchOutput impactAnalysisOutputs = uploadFiles(impactAnalysisBatch);
        logger.info("Completed the impact analysis file uploads.");

        return impactAnalysisOutputs;
    }

    private ImpactAnalysisBatchOutput uploadFiles(ImpactAnalysisBatch impactAnalysisBatch) throws BlackDuckIntegrationException {
        List<ImpactAnalysisOutput> uploadOutputs = new ArrayList<>();

        try {
            List<ImpactAnalysisCallable> callables = createCallables(impactAnalysisBatch);
            List<Future<ImpactAnalysisOutput>> submitted = new ArrayList<>();
            for (ImpactAnalysisCallable callable : callables) {
                submitted.add(executorService.submit(callable));
            }
            for (Future<ImpactAnalysisOutput> future : submitted) {
                ImpactAnalysisOutput uploadOutput = future.get();
                uploadOutputs.add(uploadOutput);
            }
        } catch (InterruptedException e) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            throw new BlackDuckIntegrationException(String.format("Encountered a problem uploading a impact analysis file: %s", e.getMessage()), e);
        } catch (Exception e) {
            throw new BlackDuckIntegrationException(String.format("Encountered a problem uploading a impact analysis file: %s", e.getMessage()), e);
        }

        return new ImpactAnalysisBatchOutput(uploadOutputs);
    }

    private List<ImpactAnalysisCallable> createCallables(ImpactAnalysisBatch impactAnalysisBatch) {
        return impactAnalysisBatch.getImpactAnalyses().stream()
            .map(impactAnalysis -> new ImpactAnalysisCallable(gson, blackDuckApiClient, apiDiscovery, impactAnalysis))
            .collect(Collectors.toList());
    }

}
