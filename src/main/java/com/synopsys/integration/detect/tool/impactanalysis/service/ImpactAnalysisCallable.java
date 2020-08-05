/**
 * blackduck-common
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
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.model.RequestFactory;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.util.NameVersion;

public class ImpactAnalysisCallable implements Callable<ImpactAnalysisOutput> {
    private final BlackDuckService blackDuckService;
    private final ImpactAnalysis impactAnalysis;
    private final NameVersion projectAndVersion;
    private final String codeLocationName;

    public ImpactAnalysisCallable(BlackDuckService blackDuckService, ImpactAnalysis impactAnalysis) {
        this.blackDuckService = blackDuckService;
        this.impactAnalysis = impactAnalysis;
        this.projectAndVersion = new NameVersion(impactAnalysis.getProjectName(), impactAnalysis.getProjectVersion());
        this.codeLocationName = impactAnalysis.getCodeLocationName();
    }

    @Override
    public ImpactAnalysisOutput call() {
        try {
            Request.Builder requestBuilder = createRequestBuilder(impactAnalysis.getImpactAnalysisPath());
            try (Response response = blackDuckService.execute(ImpactAnalysisUploadService.IMPACT_ANALYSIS_PATH, requestBuilder)) {
                return ImpactAnalysisOutput.FROM_RESPONSE(projectAndVersion, codeLocationName, response);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Failed to impact analysis file: %s because %s", impactAnalysis.getImpactAnalysisPath().toAbsolutePath(), e.getMessage());
            return ImpactAnalysisOutput.FAILURE(projectAndVersion, codeLocationName, errorMessage, e);
        }
    }

    public Request.Builder createRequestBuilder(Path reportPath) {
        Map<String, String> projectInfoMap = new HashMap<>();
        projectInfoMap.put("project", impactAnalysis.getProjectName());
        projectInfoMap.put("project-version", impactAnalysis.getProjectVersion());
        //        projectInfoMap.put("codeLocationName", impactAnalysis.getCodeLocationName());

        Map<String, File> fileMap = new HashMap<>();
        fileMap.put("file", reportPath.toFile());
        return RequestFactory.createCommonPostRequestBuilder(fileMap, projectInfoMap);
    }

}
