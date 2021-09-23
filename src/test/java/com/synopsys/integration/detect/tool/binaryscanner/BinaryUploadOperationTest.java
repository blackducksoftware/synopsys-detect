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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.common.util.finder.SimpleFileFinder;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.lifecycle.run.step.BinaryScanStepRunner;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.exception.IntegrationException;

public class BinaryUploadOperationTest {
    @Test
    public void testShouldFailOnDirectory() throws DetectUserFriendlyException {
        BinaryScanOptions binaryScanOptions = new BinaryScanOptions(Paths.get("."), Collections.singletonList(""), "", "", 0, false);
        OperationFactory operationFactory = Mockito.mock(OperationFactory.class);

        Mockito.when(operationFactory.calculateBinaryScanOptions()).thenReturn(binaryScanOptions);

        BinaryScanStepRunner binaryScanStepRunner = new BinaryScanStepRunner(operationFactory);
        Optional<File> result = binaryScanStepRunner.determineBinaryScanFileTarget(DockerTargetData.NO_DOCKER_TARGET);

        Mockito.verify(operationFactory).publishBinaryFailure(Mockito.anyString());
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void testMultipleTargetPaths() throws DetectUserFriendlyException, IOException, IntegrationException {
        SimpleFileFinder fileFinder = new SimpleFileFinder();
        DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);

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

        Mockito.when(directoryManager.getSourceDirectory()).thenReturn(rootDirectory);
        Mockito.when(directoryManager.getBinaryOutputDirectory()).thenReturn(rootDirectory);

        BinaryScanFindMultipleTargetsOperation multipleTargets = new BinaryScanFindMultipleTargetsOperation(fileFinder, directoryManager);
        Optional<File> zip = multipleTargets.searchForMultipleTargets(targetPaths, false, 3);
        Assertions.assertTrue(zip.isPresent());
        Assertions.assertTrue(zip.get().isFile());
        Assertions.assertTrue(zip.get().canRead());
    }
}
