/**
 * synopsys-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detect.util.filter;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

public class DetectOverrideableFilter implements DetectFilter {
    private final Set<String> excludedSet;
    private final Set<String> includedSet;

    public DetectOverrideableFilter(final String toExclude, final String toInclude) {
        excludedSet = createSetFromString(toExclude);
        includedSet = createSetFromString(toInclude);
    }

    public boolean shouldInclude(final String itemName) {
        if (excludedSet.contains("ALL"))
            return false;

        if (!excludedSet.contains("NONE") && excludedSet.contains(itemName)) {
            return false;
        }

        if (includedSet.size() > 0) {
            if (includedSet.contains("ALL")) {
                return true;
            } else if (includedSet.contains("NONE")) {
                return false;
            } else if (!includedSet.contains(itemName)) {
                return false;
            }
        }

        return true;
    }

    private Set<String> createSetFromString(final String s) {
        final Set<String> set = new HashSet<>();
        final StringTokenizer stringTokenizer = new StringTokenizer(StringUtils.trimToEmpty(s), ",");
        while (stringTokenizer.hasMoreTokens()) {
            set.add(StringUtils.trimToEmpty(stringTokenizer.nextToken()));
        }
        return set;
    }
}
