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
package com.synopsys.integration.detect.workflow.codelocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.util.NameVersion;

public class BdioCodeLocationCreatorTest {

    // TODO: This test seems suspiciously long and like it might not be testing much. -jp
    @Test
    public void testCreateFromDetectCodeLocations() throws IOException, DetectUserFriendlyException {

        final File sourceDir = new File("src/test/resource");

        final CodeLocationNameManager codeLocationNameManager = Mockito.mock(CodeLocationNameManager.class);
        final DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        Mockito.when(directoryManager.getSourceDirectory()).thenReturn(sourceDir);
        final EventSystem eventSystem = Mockito.mock(EventSystem.class);
        final CreateBdioCodeLocationsFromDetectCodeLocationsOperation creator = new CreateBdioCodeLocationsFromDetectCodeLocationsOperation(codeLocationNameManager, directoryManager);
        final NameVersion projectNameVersion = new NameVersion("testName", "testVersion");
        final DependencyGraph dependencyGraph = Mockito.mock(DependencyGraph.class);
        final Set<Dependency> dependencies = new HashSet<>();
        final Dependency dependency = Mockito.mock(Dependency.class);
        dependencies.add(dependency);
        Mockito.when(dependencyGraph.getRootDependencies()).thenReturn(dependencies);

        final ExternalId externalId = new ExternalId(Forge.MAVEN);
        externalId.setName("testExternalIdName");
        externalId.setVersion("testExternalIdVersion");
        externalId.setArchitecture("testExternalIdArch");
        final DetectCodeLocation detectCodeLocation = DetectCodeLocation.forCreator(dependencyGraph, sourceDir, externalId, "testCreator");
        final List<DetectCodeLocation> detectCodeLocations = new ArrayList<>();
        detectCodeLocations.add(detectCodeLocation);
        Mockito.when(codeLocationNameManager.createCodeLocationName(detectCodeLocation, sourceDir, projectNameVersion.getName(), projectNameVersion.getVersion(), "", "")).thenReturn("testCodeLocationName");

        final BdioCodeLocationResult result = creator.transformDetectCodeLocations(detectCodeLocations, "", "", projectNameVersion);

        assertEquals("testCodeLocationName", result.getBdioCodeLocations().get(0).getCodeLocationName());
        final File resultDir = result.getBdioCodeLocations().get(0).getDetectCodeLocation().getSourcePath();
        assertTrue(resultDir.getCanonicalPath().contains("test"));
        assertTrue(resultDir.getCanonicalPath().contains("resource"));
    }
}
