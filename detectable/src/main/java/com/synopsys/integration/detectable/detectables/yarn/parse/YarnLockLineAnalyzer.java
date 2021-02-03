/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.yarn.parse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YarnLockLineAnalyzer {
    private static final int SPACES_INDENT_PER_LEVEL = 2;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public int measureIndentDepth(String line) {
        if (StringUtils.isBlank(line)) {
            return 0;
        }
        int leadingSpaceCount = countLeadingSpaces(line);
        if ((leadingSpaceCount % SPACES_INDENT_PER_LEVEL) != 0) {
            logger.warn("Leading space count for '{}' is {}; expected it to be divisible by {}",
                line, leadingSpaceCount, SPACES_INDENT_PER_LEVEL);
        }
        return leadingSpaceCount / SPACES_INDENT_PER_LEVEL;
    }

    public String unquote(String s) {
        while (isQuoted(s)) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    private boolean isQuoted(String s) {
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return true;
        }
        return s.startsWith("'") && s.endsWith("'");
    }

    private int countLeadingSpaces(String line) {
        int leadingSpaceCount = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                leadingSpaceCount++;
            } else if (line.charAt(i) == '\t') {
                leadingSpaceCount += 4;
            } else {
                break;
            }
        }
        return leadingSpaceCount;
    }
}
