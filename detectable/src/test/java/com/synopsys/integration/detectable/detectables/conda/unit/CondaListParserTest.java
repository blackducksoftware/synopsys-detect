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
package com.synopsys.integration.detectable.detectables.conda.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.conda.model.CondaListElement;
import com.synopsys.integration.detectable.detectables.conda.parser.CondaListParser;

@UnitTest
public class CondaListParserTest {
    private CondaListParser condaListParser;

    @Before
    public void init() {
        condaListParser = new CondaListParser(new GsonBuilder().setPrettyPrinting().create(), new ExternalIdFactory());
    }

    @Test
    public void condaListElementToDependencyNodeTransformerTest() {
        final String platform = "linux";
        final CondaListElement element = new CondaListElement();
        element.name = "sampleName";
        element.version = "sampleVersion";
        element.buildString = "py36_0";
        final Dependency dependency = condaListParser.condaListElementToDependency(platform, element);

        assertEquals("sampleName", dependency.getName());
        assertEquals("sampleVersion-py36_0-linux", dependency.getVersion());
        assertEquals("sampleName/sampleVersion-py36_0-linux", dependency.getExternalId().createExternalId());
    }
}
