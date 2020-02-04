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
package com.synopsys.integration.detectable.detectables.yarn.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParser;

//These examples came from the babel yarn.lock
public class YarnIndentLineLevelTest {
    @Test
    public void testLineLevel0() {
        checkLineLevel("\"@types/webpack@^3.0.0\":", 0);
    }

    @Test
    public void testLineLevel1version() {
        checkLineLevel("  version \"4.0.2\"", 1);
    }

    @Test
    public void testLineLevel1deps() {
        checkLineLevel("  dependencies:", 1);
    }

    @Test
    public void testLineLevel2() {
        checkLineLevel("    \"@types/node\" \"*\"", 2);
    }

    private void checkLineLevel(final String line, final int level) {
        final YarnLockParser yarnLockParser = new YarnLockParser();
        final int actual = yarnLockParser.countIndent(line);
        Assertions.assertEquals(level, actual);
    }
}
