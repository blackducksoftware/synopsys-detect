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
package com.synopsys.integration.detectable.detectables.pear.unit;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.pear.parse.PearListParser;
import com.synopsys.integration.exception.IntegrationException;

@UnitTest
class PearListParserTest {
    private static PearListParser pearListParser;

    @BeforeEach
    void setUp() {
        pearListParser = new PearListParser();
    }

    @Test
    void parse() throws IntegrationException {
        final List<String> validListLines = Arrays.asList(
            "Installed packages, channel pear.php.net:",
            "=========================================",
            "Package              Version State",
            "Archive_Tar          1.4.3   stable",
            "Console_Getopt       1.4.1   stable"
        );

        final Map<String, String> dependenciesMap = pearListParser.parse(validListLines);
        Assertions.assertEquals(2, dependenciesMap.size());
        Assertions.assertEquals("1.4.3", dependenciesMap.get("Archive_Tar"));
        Assertions.assertEquals("1.4.1", dependenciesMap.get("Console_Getopt"));
    }

    @Test
    void parseNoStart() throws IntegrationException {
        final List<String> notStartLines = Arrays.asList(
            "Package              Version State",
            "Archive_Tar          1.4.3   stable",
            "Console_Getopt       1.4.1   stable"
        );

        final Map<String, String> dependenciesMap = pearListParser.parse(notStartLines);
        Assertions.assertEquals(0, dependenciesMap.size());
    }

    @Test
    void parseMissingInfo() {
        final List<String> missingInfoLines = Arrays.asList(
            "Installed packages, channel pear.php.net:",
            "=========================================",
            "Package              Version State",
            "Archive_Tar          1.4.3   stable",
            "Console_Getopt       "
        );

        try {
            pearListParser.parse(missingInfoLines);
            Assertions.fail("Should have thrown an exception");
        } catch (final IntegrationException ignore) {

        }
    }
}