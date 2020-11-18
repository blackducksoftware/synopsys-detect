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
package com.synopsys.integration.detectable.detectables.go.gomod;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GoModWhyParser {
    private static final String MISSING_MODULE_PREFIX = "(main module does not need module";

    public Set<String> createModuleExclusionList(List<String> lines) {
        // find lines that look like the following and extract the module name i.e. cloud.google.com/go:
        // (main module does not need module cloud.google.com/go)
        Set<String> exclusionModules = new LinkedHashSet<>();
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith(MISSING_MODULE_PREFIX)) {
                int closingParen = trimmedLine.lastIndexOf(")");
                if (closingParen > 0 && closingParen > MISSING_MODULE_PREFIX.length()) {
                    String moduleName = trimmedLine.substring(MISSING_MODULE_PREFIX.length(), closingParen);
                    exclusionModules.add(moduleName.trim());
                }
            }
        }
        return exclusionModules;
    }
}
