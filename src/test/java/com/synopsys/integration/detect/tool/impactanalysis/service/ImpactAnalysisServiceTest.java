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

import org.junit.jupiter.api.Test;

import com.synopsys.integration.exception.IntegrationException;

class ImpactAnalysisServiceTest {
    // TODO
    @Test
    void createRequestTest() throws IntegrationException {
        //        Path reportPath = Paths.get("testPath");
        //        final String baseUrl = "https://blackduck.test.com";
        //        String expectedEndpoint = baseUrl + ImpactAnalysisUploadService.IMPACT_ANALYSIS_PATH;
        //
        //        BlackDuckService blackDuckService = Mockito.mock(BlackDuckService.class);
        //        Mockito.when(blackDuckService.getUri(ImpactAnalysisUploadService.IMPACT_ANALYSIS_PATH)).thenReturn(expectedEndpoint);
        //
        //        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //        ImpactAnalysisUploadService impactAnalysisService = new ImpactAnalysisUploadService(impactAnalysisBatchRunner, codeLocationCreationService);
        //
        //        Request request = impactAnalysisService.createRequest(Paths.get("testPath"));
        //        MultipartBodyContent bodyContent = (MultipartBodyContent) request.getBodyContent();
        //        Map<String, File> bodyContentFileMap = bodyContent.getBodyContentFileMap();
        //
        //        Assertions.assertEquals(expectedEndpoint, request.getUri(), "The URL may have been constructed incorrectly.");
        //        Assertions.assertTrue(bodyContentFileMap.containsKey("file"), "Black Duck expects a multipart form with the file attribute.");
        //        Assertions.assertEquals(reportPath, bodyContentFileMap.get("file").toPath());
    }
}
