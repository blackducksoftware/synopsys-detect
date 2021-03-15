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
package com.synopsys.integration.detect.tool.binaryscanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatch;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanOutput;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanUploadService;
import com.synopsys.integration.common.util.finder.SimpleFileFinder;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckBinaryScannerToolTest {

    @Test
    public void testShouldRunFalsePropertyNotSet() {
        BinaryScanOptions binaryScanOptions = new BinaryScanOptions(null, Collections.singletonList(""), "", "", 0);

        BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(null, null, null, null, binaryScanOptions, null);
        boolean shouldRunResponse = tool.shouldRun();

        assertFalse(shouldRunResponse);
    }

    @Test
    public void testShouldRunTrueFileNonExistent() {
        BinaryScanOptions binaryScanOptions = new BinaryScanOptions(Paths.get("thisisnotafile"), Collections.singletonList(""), "", "", 0);

        BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(null, null, null, null, binaryScanOptions, null);

        boolean shouldRunResponse = tool.shouldRun();

        assertTrue(shouldRunResponse);
    }

    @Test
    public void testShouldRunTruePropertySetToDirectory() {
        BinaryScanOptions binaryScanOptions = new BinaryScanOptions(Paths.get("."), Collections.singletonList(""), "", "", 0);

        BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(null, null, null, null, binaryScanOptions, null);

        boolean shouldRunResponse = tool.shouldRun();

        assertTrue(shouldRunResponse);
    }

    @Test
    public void testShouldRunTrueEverythingCorrect() throws IOException {
        File binaryScanFile = Files.createTempFile("test", "binaryScanFile").toFile();
        binaryScanFile.deleteOnExit();
        assertTrue(binaryScanFile.canRead());
        assertTrue(binaryScanFile.exists());

        BinaryScanOptions binaryScanOptions = new BinaryScanOptions(binaryScanFile.toPath(), Collections.singletonList(""), "", "", 0);

        BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(null, null, null, null, binaryScanOptions, null);

        boolean shouldRunResponse = tool.shouldRun();

        assertTrue(shouldRunResponse);
    }

    @Test
    public void testShouldFailOnDirectory() throws DetectUserFriendlyException {
        BinaryScanOptions binaryScanOptions = new BinaryScanOptions(Paths.get("."), Collections.singletonList(""), "", "", 0);

        EventSystem eventSystem = Mockito.mock(EventSystem.class);

        BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(eventSystem, null, null, null, binaryScanOptions, null);

        NameVersion projectNameVersion = new NameVersion("testName", "testVersion");

        BinaryScanToolResult result = tool.performBinaryScanActions(projectNameVersion);

        assertFalse(result.isSuccessful());
    }

    @Test
    public void testMultipleTargetPaths() throws DetectUserFriendlyException, IOException, IntegrationException {
        SimpleFileFinder fileFinder = new SimpleFileFinder();
        DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        BinaryScanUploadService uploadService = Mockito.mock(BinaryScanUploadService.class);
        CodeLocationNameManager codeLocationNameManager = Mockito.mock(CodeLocationNameManager.class);
        EventSystem eventSystem = Mockito.mock(EventSystem.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.isStatusCodeSuccess()).thenReturn(true);

        File rootDirectory = Files.createTempDirectory("BinaryScannerTest").toFile();
        File subDirectory = new File(rootDirectory, "BinaryScannerSubDirectory");
        File binaryFile_1 = new File(subDirectory, "binaryTestFile_1.txt");
        File binaryFile_2 = new File(subDirectory, "binaryTestFile_2.text");
        FileUtils.write(binaryFile_1, "binary test file 1", StandardCharsets.UTF_8);
        FileUtils.write(binaryFile_2, "binary test file 2", StandardCharsets.UTF_8);
        subDirectory.mkdirs();
        ArrayList<String> targetPaths = new ArrayList<>();
        targetPaths.add("binaryTestFile_1.txt");
        targetPaths.add("*.text");

        String codeLocationName = "CodeLocationName";
        NameVersion projectNameVersion = new NameVersion("testName", "testVersion");
        BinaryScanOutput binaryScanOutput = BinaryScanOutput.FROM_RESPONSE(projectNameVersion, codeLocationName, response);
        BinaryScanBatchOutput binaryScanOutputs = new BinaryScanBatchOutput(Collections.singletonList(binaryScanOutput));
        CodeLocationCreationData<BinaryScanBatchOutput> expectedOutput = new CodeLocationCreationData<BinaryScanBatchOutput>(null, binaryScanOutputs);

        Mockito.when(directoryManager.getSourceDirectory()).thenReturn(rootDirectory);
        Mockito.when(directoryManager.getBinaryOutputDirectory()).thenReturn(rootDirectory);
        Mockito.when(codeLocationNameManager.createBinaryScanCodeLocationName(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
            .thenReturn(codeLocationName);
        Mockito.doAnswer(invocation -> expectedOutput).when(uploadService).uploadBinaryScan(Mockito.any(BinaryScanBatch.class));

        BinaryScanOptions binaryScanOptions = new BinaryScanOptions(null, targetPaths, "", "", 3);
        BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(eventSystem, codeLocationNameManager, directoryManager, fileFinder, binaryScanOptions, uploadService);

        BinaryScanToolResult result = tool.performBinaryScanActions(projectNameVersion);

        assertTrue(result.isSuccessful());
    }
}
