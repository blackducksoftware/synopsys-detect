/**
 * synopsys-detect
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
package com.synopsys.integration.detect.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.configuration.help.DetectArgumentState;
import com.synopsys.integration.detect.configuration.help.DetectArgumentStateParser;

public class ArgumentStateParserTests {
    private final DetectArgumentStateParser parser = new DetectArgumentStateParser();

    @Test
    public void helpParsesValue() {
        final String[] args = new String[] { "-h", "value" };
        final DetectArgumentState state = parser.parseArgs(args);

        Assertions.assertTrue(state.isHelp());
        Assertions.assertEquals("value", state.getParsedValue());
    }

    @Test
    public void helpIgnoresDash() {
        final String[] args = new String[] { "-h", "-ignoreme" };
        final DetectArgumentState state = parser.parseArgs(args);

        Assertions.assertTrue(state.isHelp());
        Assertions.assertNull(state.getParsedValue());
    }

    @Test
    public void helpParsesInMiddleWithNoValue() {
        final String[] args = new String[] { "--propert", "--property", "-h", "--property", "--property" };
        final DetectArgumentState state = parser.parseArgs(args);

        Assertions.assertTrue(state.isHelp());
        Assertions.assertNull(state.getParsedValue());
    }

    @Test
    public void helpParsesEndValue() {
        final String[] args = new String[] { "--property", "--property", "-h", "value" };
        final DetectArgumentState state = parser.parseArgs(args);

        Assertions.assertTrue(state.isHelp());
        Assertions.assertEquals("value", state.getParsedValue());
    }

    @Test
    public void helpParsesStartValue() {
        final String[] args = new String[] { "-h", "value", "--property", "--property", "--property" };
        final DetectArgumentState state = parser.parseArgs(args);

        Assertions.assertTrue(state.isHelp());
        Assertions.assertEquals("value", state.getParsedValue());
    }

}
