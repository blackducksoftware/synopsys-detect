/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.tool.impactanalysis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.log.IntLogger;

public class ImpactAnalysisBatchRunner {
    private final IntLogger logger;
    private final BlackDuckApiClient blackDuckService;
    private final ExecutorService executorService;
    private final Gson gson;

    public ImpactAnalysisBatchRunner(IntLogger logger, BlackDuckApiClient blackDuckService, ExecutorService executorService, Gson gson) {
        this.logger = logger;
        this.blackDuckService = blackDuckService;
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
        } catch (Exception e) {
            throw new BlackDuckIntegrationException(String.format("Encountered a problem uploading a impact analysis file: %s", e.getMessage()), e);
        }

        return new ImpactAnalysisBatchOutput(uploadOutputs);
    }

    private List<ImpactAnalysisCallable> createCallables(ImpactAnalysisBatch impactAnalysisBatch) {
        return impactAnalysisBatch.getImpactAnalyses().stream()
                   .map(impactAnalysis -> new ImpactAnalysisCallable(gson, blackDuckService, impactAnalysis, BlackDuckServicesFactory.createDefaultRequestFactory()))
                   .collect(Collectors.toList());
    }

}
