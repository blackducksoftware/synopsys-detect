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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckBinaryScannerToolTest {

    @Test
    public void testShouldRunFalsePropertyNotSet() throws DetectUserFriendlyException {
        final BinaryScanOptions binaryScanOptions = new BinaryScanOptions(null, Collections.singletonList(""), "", "");

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(null, null, null, null, binaryScanOptions, null);
        final boolean shouldRunResponse = !tool.determineBinaryScanPaths(DockerTargetData.NO_DOCKER_TARGET, new NameVersion("name", "version")).isEmpty();

        assertFalse(shouldRunResponse);
    }

    @Test
    public void testShouldRunTrueFileNonExistent() throws DetectUserFriendlyException {
        final BinaryScanOptions binaryScanOptions = new BinaryScanOptions(Paths.get("thisisnotafile"), Collections.singletonList(""), "", "");

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(null, null, null, null, binaryScanOptions, null);

        final boolean shouldRunResponse = !tool.determineBinaryScanPaths(DockerTargetData.NO_DOCKER_TARGET, new NameVersion("name", "version")).isEmpty();;

        assertTrue(shouldRunResponse);
    }

    @Test
    public void testShouldRunTruePropertySetToDirectory() throws DetectUserFriendlyException {
        final BinaryScanOptions binaryScanOptions = new BinaryScanOptions(Paths.get("."), Collections.singletonList(""), "", "");

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(null, null, null, null, binaryScanOptions, null);

        final boolean shouldRunResponse = !tool.determineBinaryScanPaths(DockerTargetData.NO_DOCKER_TARGET, new NameVersion("name", "version")).isEmpty();;

        assertTrue(shouldRunResponse);
    }

    @Test
    public void testShouldRunTrueEverythingCorrect() throws IOException, DetectUserFriendlyException {
        final File binaryScanFile = Files.createTempFile("test", "binaryScanFile").toFile();
        binaryScanFile.deleteOnExit();
        assertTrue(binaryScanFile.canRead());
        assertTrue(binaryScanFile.exists());

        final BinaryScanOptions binaryScanOptions = new BinaryScanOptions(binaryScanFile.toPath(), Collections.singletonList(""), "", "");

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(null, null, null, null, binaryScanOptions, null);

        final boolean shouldRunResponse = !tool.determineBinaryScanPaths(DockerTargetData.NO_DOCKER_TARGET, new NameVersion("name", "version")).isEmpty();;

        assertTrue(shouldRunResponse);
    }

    @Test
    public void testShouldFailOnDirectory() throws DetectUserFriendlyException {
        final BinaryScanOptions binaryScanOptions = new BinaryScanOptions(Paths.get("."), Collections.singletonList(""), "", "");

        final EventSystem eventSystem = Mockito.mock(EventSystem.class);

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(eventSystem, null, null, null, binaryScanOptions, null);

        Map<File, NameVersion> scanPaths = tool.determineBinaryScanPaths(DockerTargetData.NO_DOCKER_TARGET, new NameVersion("testName", "testVersion"));

        final BinaryScanToolResult result = tool.performBinaryScanActions(scanPaths).get(0);

        assertFalse(result.isSuccessful());
    }
}
