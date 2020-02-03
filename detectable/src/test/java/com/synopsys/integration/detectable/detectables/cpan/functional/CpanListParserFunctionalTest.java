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
package com.synopsys.integration.detectable.detectables.cpan.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.cpan.parse.CpanListParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

@FunctionalTest
public class CpanListParserFunctionalTest {
    private final CpanListParser cpanListParser = new CpanListParser(new ExternalIdFactory());

    @Test
    public void getDirectModuleNamesTest() {
        final List<String> showDepsText = FunctionalTestFiles.asListOfStrings("/cpan/showDeps.txt");
        final List<String> names = cpanListParser.getDirectModuleNames(showDepsText);

        assertEquals(4, names.size());
        assertTrue(names.contains("ExtUtils::MakeMaker"));
        assertTrue(names.contains("Test::More"));
        assertTrue(names.contains("perl"));
        assertTrue(names.contains("ExtUtils::MakeMaker"));
    }

    @Test
    public void makeDependencyNodesTest() {
        final List<String> cpanListText = FunctionalTestFiles.asListOfStrings("/cpan/cpanList.txt");
        final List<String> showDepsText = FunctionalTestFiles.asListOfStrings("/cpan/showDeps.txt");

        final DependencyGraph dependencyGraph = cpanListParser.parse(cpanListText, showDepsText);

        GraphCompare.assertEqualsResource("/cpan/expectedDependencyNodes_graph.json", dependencyGraph);
    }
}
