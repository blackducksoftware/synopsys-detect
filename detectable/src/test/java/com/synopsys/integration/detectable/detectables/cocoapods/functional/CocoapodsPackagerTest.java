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
package com.synopsys.integration.detectable.detectables.cocoapods.functional;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.cocoapods.parser.PodlockParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

@FunctionalTest
public class CocoapodsPackagerTest {
    private final PodlockParser podlockParser = new PodlockParser(new ExternalIdFactory());

    @Test
    @Disabled
    public void simpleTest() throws IOException, MissingExternalIdException {
        final String podlockText = FunctionalTestFiles.asString("/cocoapods/simplePodfile.lock");
        final DependencyGraph projectDependencies = podlockParser.extractDependencyGraph(podlockText);
        GraphCompare.assertEqualsResource("/cocoapods/simpleExpected_graph.json", projectDependencies);
    }

    @Test
    @Disabled
    public void complexTest() throws IOException, MissingExternalIdException {
        final String podlockText = FunctionalTestFiles.asString("/cocoapods/complexPodfile.lock");
        final DependencyGraph projectDependencies = podlockParser.extractDependencyGraph(podlockText);
        GraphCompare.assertEqualsResource("/cocoapods/complexExpected_graph.json", projectDependencies);
    }
}
