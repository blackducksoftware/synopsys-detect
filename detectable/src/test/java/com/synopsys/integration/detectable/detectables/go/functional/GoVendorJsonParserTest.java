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
package com.synopsys.integration.detectable.detectables.go.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.go.vendor.parse.GoVendorJsonParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public class GoVendorJsonParserTest {

    @Test
    public void test() throws IOException {
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final GoVendorJsonParser parser = new GoVendorJsonParser(externalIdFactory);
        final DependencyGraph graph = parser.parseVendorJson(new Gson(), FunctionalTestFiles.asString("/go/vendor/vendor.json"));
        assertEquals(2, graph.getRootDependencies().size());
        boolean foundErrorsPkg = false;
        boolean foundMathPkg = false;
        for (final Dependency dep : graph.getRootDependencies()) {
            if ("github.com/pkg/errors".equals(dep.getName())) {
                foundErrorsPkg = true;
                assertEquals("github.com/pkg/errors", dep.getExternalId().getName());
                assertEquals("059132a15dd08d6704c67711dae0cf35ab991756", dep.getExternalId().getVersion());
            }
            if ("github.com/pkg/math".equals(dep.getName())) {
                foundMathPkg = true;
                assertEquals("github.com/pkg/math", dep.getExternalId().getName());
                assertEquals("f2ed9e40e245cdeec72c4b642d27ed4553f90667", dep.getExternalId().getVersion());
            }
        }
        assertTrue(foundErrorsPkg);
        assertTrue(foundMathPkg);
    }
}
