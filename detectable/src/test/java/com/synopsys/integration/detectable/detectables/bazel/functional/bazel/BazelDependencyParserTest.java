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
package com.synopsys.integration.detectable.detectables.bazel.functional.bazel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
//import com.synopsys.integration.detectable.detectables.bazel.BazelDependencyParser;

public class BazelDependencyParserTest {

//    @Test
//    public void test() {
//        final ExternalIdFactory externalIdFactory = Mockito.mock(ExternalIdFactory.class);
//        final BazelDependencyParser bazelDependencyParser = new BazelDependencyParser(externalIdFactory);
//        // externalIdFactory.createMavenExternalId(group, artifact, version);
//        final ExternalId testExternalId = new ExternalId(Forge.MAVEN);
//        testExternalId.setGroup("testgroup");
//        testExternalId.setName("testartifact");
//        testExternalId.setVersion("testversion");
//        Mockito.when(externalIdFactory.createMavenExternalId("testgroup", "testartifact", "testversion")).thenReturn(testExternalId);
//
//        final Dependency dependency = bazelDependencyParser.gavStringToDependency("testgroup:testartifact:testversion", ":");
//
//        assertEquals("testartifact", dependency.getExternalId().getName());
//    }
}
