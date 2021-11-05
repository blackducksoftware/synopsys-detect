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
package com.synopsys.integration.detectable.detectables.pip.inspector.unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.pipenv.model.PipFreeze;
import com.synopsys.integration.detectable.detectables.pipenv.model.PipFreezeEntry;
import com.synopsys.integration.detectable.detectables.pipenv.model.PipenvGraph;
import com.synopsys.integration.detectable.detectables.pipenv.model.PipenvGraphDependency;
import com.synopsys.integration.detectable.detectables.pipenv.model.PipenvGraphEntry;
import com.synopsys.integration.detectable.detectables.pipenv.parser.PipenvTransformer;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

@UnitTest
public class PipenvTransformerTest {
    @Test
    void resolvesFuzzyVersion() {
        List<PipFreezeEntry> pipFreezeEntries = new ArrayList<>();
        pipFreezeEntries.add(new PipFreezeEntry("example", "2.0.0"));
        PipFreeze pipFreeze = new PipFreeze(pipFreezeEntries);

        List<PipenvGraphEntry> pipenvGraphEntries = new ArrayList<>();
        pipenvGraphEntries.add(new PipenvGraphEntry("example", "fuzzy", new ArrayList<>()));
        PipenvGraph pipenvGraph = new PipenvGraph(pipenvGraphEntries);

        PipenvTransformer pipenvTransformer = new PipenvTransformer(new ExternalIdFactory());
        CodeLocation codeLocation = pipenvTransformer.transform("", "", pipFreeze, pipenvGraph, false);
        DependencyGraph graph = codeLocation.getDependencyGraph();

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, graph);
        graphAssert.hasDependency("example", "2.0.0");
    }

    @Test
    void resolvesLowercaseNameWithFreezeCapital() {
        List<PipFreezeEntry> pipFreezeEntries = new ArrayList<>();
        pipFreezeEntries.add(new PipFreezeEntry("Example", "2.0.0"));
        PipFreeze pipFreeze = new PipFreeze(pipFreezeEntries);

        List<PipenvGraphEntry> pipenvGraphEntries = new ArrayList<>();
        pipenvGraphEntries.add(new PipenvGraphEntry("example", "fuzzy", new ArrayList<>()));
        PipenvGraph pipenvGraph = new PipenvGraph(pipenvGraphEntries);

        PipenvTransformer pipenvTransformer = new PipenvTransformer(new ExternalIdFactory());
        CodeLocation codeLocation = pipenvTransformer.transform("", "", pipFreeze, pipenvGraph, false);
        DependencyGraph graph = codeLocation.getDependencyGraph();

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, graph);
        graphAssert.hasDependency("Example", "2.0.0");
    }

    @Test
    void usesProjectDependencyAsRoot() {
        PipFreeze pipFreeze = new PipFreeze(new ArrayList<>());

        List<PipenvGraphEntry> pipenvGraphEntries = new ArrayList<>();
        List<PipenvGraphDependency> children = new ArrayList<>();
        children.add(new PipenvGraphDependency("shouldBeAtRoot", "shouldbeAtRootVersion", new ArrayList<>()));
        pipenvGraphEntries.add(new PipenvGraphEntry("projectName", "projectVersion", children));
        PipenvGraph pipenvGraph = new PipenvGraph(pipenvGraphEntries);

        PipenvTransformer pipenvTransformer = new PipenvTransformer(new ExternalIdFactory());
        CodeLocation codeLocation = pipenvTransformer.transform("projectName", "projectVersion", pipFreeze, pipenvGraph, false);
        DependencyGraph graph = codeLocation.getDependencyGraph();

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, graph);
        graphAssert.hasRootDependency("shouldBeAtRoot", "shouldbeAtRootVersion");
    }

    @Test
    void ignoresNonProject() {
        PipFreeze pipFreeze = new PipFreeze(new ArrayList<>());

        List<PipenvGraphEntry> pipenvGraphEntries = new ArrayList<>();
        pipenvGraphEntries.add(new PipenvGraphEntry("projectName", "projectVersion", Collections.singletonList(new PipenvGraphDependency("child", "childVersion", Collections.emptyList()))));
        pipenvGraphEntries.add(new PipenvGraphEntry("non-projectName", "non-projectVersion", new ArrayList<>()));
        PipenvGraph pipenvGraph = new PipenvGraph(pipenvGraphEntries);

        PipenvTransformer pipenvTransformer = new PipenvTransformer(new ExternalIdFactory());
        CodeLocation codeLocation = pipenvTransformer.transform("projectName", "projectVersion", pipFreeze, pipenvGraph, true);
        DependencyGraph graph = codeLocation.getDependencyGraph();

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, graph);
        graphAssert.hasRootDependency("child", "childVersion");
        graphAssert.hasNoDependency("non-projectName", "non-projectVersion");
    }
}
