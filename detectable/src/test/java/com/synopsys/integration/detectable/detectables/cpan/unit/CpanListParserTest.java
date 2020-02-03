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
package com.synopsys.integration.detectable.detectables.cpan.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.cpan.parse.CpanListParser;

@UnitTest
public class CpanListParserTest {
    private final CpanListParser cpanListParser = new CpanListParser(new ExternalIdFactory());

    @Test
    public void parseTest() {
        final List<String> tokens = new ArrayList<>();
        tokens.add("Test::More\t1.2.3");
        tokens.add("Test::Less\t1.2.4");
        tokens.add("This is an invalid line");
        tokens.add("This\t1\t1also\t1invalid");
        tokens.add("Invalid");

        final Map<String, String> nodeMap = cpanListParser.createNameVersionMap(tokens);
        assertEquals(2, nodeMap.size());
        assertNotNull(nodeMap.get("Test::More"));
        assertNotNull(nodeMap.get("Test::Less"));
        assertEquals("1.2.3", nodeMap.get("Test::More"));
        assertEquals("1.2.4", nodeMap.get("Test::Less"));
    }
}
