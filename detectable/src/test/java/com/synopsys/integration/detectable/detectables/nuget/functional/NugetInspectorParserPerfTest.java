/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.nuget.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.BdioNodeFactory;
import com.synopsys.integration.bdio.BdioPropertyHelper;
import com.synopsys.integration.bdio.graph.DependencyGraphTransformer;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.BdioNode;
import com.synopsys.integration.bdio.model.BdioProject;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.nuget.parse.NugetInspectorParser;
import com.synopsys.integration.detectable.detectables.nuget.parse.NugetParseResult;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public class NugetInspectorParserPerfTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Disabled
    @Test
    public void performanceTestNuget() {
        Assertions.assertTimeout(Duration.ofSeconds(120), () -> {
            final String dependencyGraphFile = FunctionalTestFiles.asString("/nuget/dwCheckApi_inspection.json");

            final NugetInspectorParser packager = new NugetInspectorParser(gson, externalIdFactory);

            final NugetParseResult result = packager.createCodeLocation(dependencyGraphFile);
            final CodeLocation codeLocation = result.getCodeLocations().get(0);

            final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
            final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
            final DependencyGraphTransformer dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);

            final BdioProject bdioNode = bdioNodeFactory.createProject("test", "1.0.0", BdioId.createFromPieces("bdioId"), externalIdFactory.createMavenExternalId("group", "name", "version"));

            final List<BdioComponent> components = dependencyGraphTransformer
                                                       .transformDependencyGraph(codeLocation.getDependencyGraph(), bdioNode, codeLocation.getDependencyGraph().getRootDependencies(), new HashMap<ExternalId, BdioNode>());

            assertEquals(211, components.size());
        });
    }
}
