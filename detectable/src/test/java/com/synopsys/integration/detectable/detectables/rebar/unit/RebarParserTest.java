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
package com.synopsys.integration.detectable.detectables.rebar.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.rebar.parse.Rebar3TreeParser;

@UnitTest
public class RebarParserTest {
    private static Rebar3TreeParser rebar3TreeParser;
    private static ExternalIdFactory externalIdFactory;

    @BeforeAll
    static void setup() {
        externalIdFactory = new ExternalIdFactory();
        rebar3TreeParser = new Rebar3TreeParser(externalIdFactory);
    }

    @Test
    void testCreateDependencyFromLine() {
        final String expectedName = "cf";
        final String expectedVersion = "0.2.2";
        final ExternalId expectedExternalId = externalIdFactory.createNameVersionExternalId(Forge.HEX, expectedName, expectedVersion);

        final Dependency actualDependency = rebar3TreeParser.createDependencyFromLine("   \u2502  \u2502  \u2514\u2500 cf\u25000.2.2 (hex package)");

        assertEquals(expectedName, actualDependency.getName());
        assertEquals(expectedVersion, actualDependency.getVersion());
        assertEquals(expectedExternalId.getName(), actualDependency.getExternalId().getName());
        assertEquals(expectedExternalId.getVersion(), actualDependency.getExternalId().getVersion());
    }

    @Test
    void testReduceLineToNameVersion() {
        assertEquals("qdate\u25000.4.2", rebar3TreeParser.reduceLineToNameVersion("   \u251C\u2500 qdate\u25000.4.2 (git repo)"));
        assertEquals("erlware_commons\u25001.0.1", rebar3TreeParser.reduceLineToNameVersion("   \u2502  \u251C\u2500 erlware_commons\u25001.0.1 (hex package)"));
        assertEquals("cf\u25000.2.2", rebar3TreeParser.reduceLineToNameVersion("   \u2502  \u2502  \u2514\u2500 cf\u25000.2.2 (hex package)"));
    }

    @Test
    void testGetDependencyLevelFromLine() {
        assertEquals(0, rebar3TreeParser.getDependencyLevelFromLine("\u2514\u2500 gcm\u25001.0.1 (project app)"));
        assertEquals(1, rebar3TreeParser.getDependencyLevelFromLine("   \u251C\u2500 qdate\u25000.4.2 (git repo)"));
        assertEquals(2, rebar3TreeParser.getDependencyLevelFromLine("   \u2502  \u251C\u2500 erlware_commons\u25001.0.1 (hex package)"));
        assertEquals(3, rebar3TreeParser.getDependencyLevelFromLine("   \u2502  \u2502  \u2514\u2500 cf\u25000.2.2 (hex package)"));
        assertEquals(2, rebar3TreeParser.getDependencyLevelFromLine("   \u2502  \u2514\u2500 qdate_localtime\u25001.1.0 (hex package)"));
        assertEquals(1, rebar3TreeParser.getDependencyLevelFromLine("   \u2514\u2500 webpush_encryption\u25000.0.1 (git repo)"));
        assertEquals(2, rebar3TreeParser.getDependencyLevelFromLine("      \u2514\u2500 base64url\u25000.0.1 (git repo)"));
    }

    @Test
    void testIsProjectLine() {
        assertTrue(rebar3TreeParser.isProject("\u2514\u2500 gcm\u25001.0.1 (project app)"));
        assertFalse(rebar3TreeParser.isProject("   \u251C\u2500 qdate\u25000.4.2 (git repo)"));
        assertFalse(rebar3TreeParser.isProject("   \u2502  \u251C\u2500 erlware_commons\u25001.0.1 (hex package)"));
    }
}
