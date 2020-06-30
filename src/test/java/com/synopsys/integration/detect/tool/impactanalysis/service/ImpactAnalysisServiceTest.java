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

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.detect.testutils.TestUtil;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.response.Response;

class ImpactAnalysisServiceTest {

    @Test
    void uploadImpactAnalysisReport_Success() throws IntegrationException, IOException {
        BlackDuckService blackDuckService = Mockito.mock(BlackDuckService.class);
        Mockito.when(blackDuckService.getUri(Mockito.any())).thenReturn("testPath");

        Response response = Mockito.mock(Response.class);
        Mockito.when(response.isStatusCodeSuccess()).thenReturn(true);
        String successResponseContent = new TestUtil().getResourceAsUTF8String("/impact-analysis/impact-analysys-success-response.json");
        Mockito.when(response.getContentString()).thenReturn(successResponseContent);

        Mockito.when(blackDuckService.execute(Mockito.any())).thenReturn(response);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ImpactAnalysisService impactAnalysisService = new ImpactAnalysisService(blackDuckService, gson);

        ImpactAnalysisUploadResult result = impactAnalysisService.uploadImpactAnalysisReport(Paths.get("somepath"));
        Assertions.assertFalse(result.getImpactAnalysisErrorResult().isPresent());
        Assertions.assertTrue(result.getImpactAnalysisSuccessResult().isPresent());

        ImpactAnalysisSuccessResult successResult = result.getImpactAnalysisSuccessResult().get();
        Assertions.assertEquals("86e41184-d7fe-411d-a545-8f056c0e8d01", successResult.codeLocationId);
        Assertions.assertEquals("vers", successResult.scannerVersion);
        Assertions.assertEquals("vers", successResult.signatureVersion);
        Assertions.assertEquals("73d82c1a-c16a-40b5-a26a-8934ae8e718e", successResult.id);
        Assertions.assertEquals("CALL_GRAPH", successResult.scanType);
        Assertions.assertEquals("synopsys-detect/synopsys-detect/Default Detect Version impact_analysis", successResult.codeLocationName);
        Assertions.assertEquals("jakem-mac", successResult.hostName);
        Assertions.assertEquals("/Users/jakem/workspace/synopsys-detect", successResult.baseDir);
        Assertions.assertEquals("token", successResult.ownerEntityKeyToken);
        Assertions.assertEquals("2020-06-29T15:58:28.233Z", successResult.createdOn);
        Assertions.assertEquals(new Integer(1), successResult.timeToScan);
        Assertions.assertEquals("00000000-0000-0000-0001-000000000001", successResult.createdByUserId);
        Assertions.assertEquals("ERROR", successResult.status);
        Assertions.assertEquals("502 Bad Gateway", successResult.statusMessage);
        Assertions.assertEquals(new Integer(0), successResult.matchCount);
        Assertions.assertEquals(new Integer(0), successResult.numberOfDirectories);
        Assertions.assertEquals(new Integer(0), successResult.numberOfNonDirectoryFiles);
        Assertions.assertEquals("CG", successResult.scanSourceType);
        Assertions.assertEquals("73d82c1a-c16a-40b5-a26a-8934ae8e718e", successResult.scanSourceId);
        Assertions.assertEquals(new Integer(0), successResult.scanTime);
        Assertions.assertEquals(new Integer(0), successResult.timeLastModified);
        Assertions.assertEquals(new Integer(0), successResult.timeToPersistMs);
        Assertions.assertNotNull(successResult.arguments);
    }

    @Test
    void uploadImpactAnalysisReport_Failure() throws IntegrationException, IOException {
        BlackDuckService blackDuckService = Mockito.mock(BlackDuckService.class);
        Mockito.when(blackDuckService.getUri(Mockito.any())).thenReturn("testPath");

        Response response = Mockito.mock(Response.class);
        Mockito.when(response.isStatusCodeSuccess()).thenReturn(false);
        String successResponseContent = new TestUtil().getResourceAsUTF8String("/impact-analysis/impact-analysys-failure-response.json");
        Mockito.when(response.getContentString()).thenReturn(successResponseContent);

        Mockito.when(blackDuckService.execute(Mockito.any())).thenReturn(response);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ImpactAnalysisService impactAnalysisService = new ImpactAnalysisService(blackDuckService, gson);

        ImpactAnalysisUploadResult result = impactAnalysisService.uploadImpactAnalysisReport(Paths.get("somepath"));
        Assertions.assertTrue(result.getImpactAnalysisErrorResult().isPresent());
        Assertions.assertFalse(result.getImpactAnalysisSuccessResult().isPresent());

        ImpactAnalysisErrorResult errorResult = result.getImpactAnalysisErrorResult().get();
        Assertions.assertEquals("ERROR_SAVING_SCAN_DATA", errorResult.status);
        Assertions.assertEquals("Made up error message.", errorResult.errorMessage);
        Assertions.assertEquals(new Integer(0), errorResult.matchCount);
        Assertions.assertEquals(new Integer(0), errorResult.numberOfDirectories);
        Assertions.assertEquals(new Integer(0), errorResult.numberOfNonDirectoryFiles);
        Assertions.assertEquals(new Integer(0), errorResult.scanTime);
        Assertions.assertEquals(new Integer(0), errorResult.timeLastModified);
        Assertions.assertEquals(new Integer(0), errorResult.timeToPersistMs);
    }
}