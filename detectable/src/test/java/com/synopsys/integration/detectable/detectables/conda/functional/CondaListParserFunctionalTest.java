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
package com.synopsys.integration.detectable.detectables.conda.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.conda.parser.CondaListParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

@FunctionalTest
public class CondaListParserFunctionalTest {
    private CondaListParser condaListParser;

    @BeforeEach
    public void init() {
        condaListParser = new CondaListParser(new GsonBuilder().setPrettyPrinting().create(), new ExternalIdFactory());
    }

    @Test
    public void smallParseTest() {
        final String condaInfoJson = FunctionalTestFiles.asString("/conda/condaInfo.json");
        final String condaListJson = FunctionalTestFiles.asString("/conda/condaListSmall.json");
        final DependencyGraph dependencyGraph = condaListParser.parse(condaListJson, condaInfoJson);

        GraphCompare.assertEqualsResource("/conda/condaListSmallExpected_graph.json", dependencyGraph);
    }

    @Test
    public void largeParseTest() {
        final String condaInfoJson = FunctionalTestFiles.asString("/conda/condaInfo.json");
        final String condaListJson = FunctionalTestFiles.asString("/conda/condaListLarge.json");
        final DependencyGraph dependencyGraph = condaListParser.parse(condaListJson, condaInfoJson);

        GraphCompare.assertEqualsResource("/conda/condaListLargeExpected_graph.json", dependencyGraph);
    }
}
