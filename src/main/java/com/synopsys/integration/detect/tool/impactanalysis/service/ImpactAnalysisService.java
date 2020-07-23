/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
            if (response.isStatusCodeSuccess()) {
                return ImpactAnalysisUploadResult.success(gson.fromJson(response.getContentString(), ImpactAnalysisSuccessResult.class));
            } else {
                return ImpactAnalysisUploadResult.failure(gson.fromJson(response.getContentString(), ImpactAnalysisErrorResult.class));
            }
        }
    }
}
