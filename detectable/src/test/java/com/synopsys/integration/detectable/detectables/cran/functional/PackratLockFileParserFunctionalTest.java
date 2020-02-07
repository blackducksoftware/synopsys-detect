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
package com.synopsys.integration.detectable.detectables.cran.functional;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.cran.parse.PackratLockFileParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

class PackratLockFileParserFunctionalTest {
    @Test
    @Disabled
    void parseProjectDependencies() throws MissingExternalIdException {
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final PackratLockFileParser packRatLockFileParser = new PackratLockFileParser(externalIdFactory);
        final List<String> packratFileLines = FunctionalTestFiles.asListOfStrings("/cran/packrat.lock");
        final DependencyGraph actualDependencyGraph = packRatLockFileParser.parseProjectDependencies(packratFileLines);

        GraphCompare.assertEqualsResource("/cran/expectedDependencyGraph.json", actualDependencyGraph);
    }
}