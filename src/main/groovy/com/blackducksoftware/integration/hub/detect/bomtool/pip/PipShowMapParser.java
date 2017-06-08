/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.bomtool.pip;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class PipShowMapParser {
    public Map<String, String> parse(final String pipShowText) {
        final Map<String, String> map = new HashMap<>();
        for (final String line : pipShowText.split("\n")) {
            if (StringUtils.isNotEmpty(line)) {
                final Entry<String, String> entry = lineToEntry(line);
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    public Entry<String, String> lineToEntry(final String line) {
        Entry<String, String> entry = null;
        final String objectIdenetifier = ":";
        if (line.contains(objectIdenetifier)) {
            final List<String> lineSegments = new ArrayList<>(Arrays.asList(line.split(objectIdenetifier)));
            final String key = lineSegments.remove(0).trim();
            final String value = StringUtils.join(lineSegments, objectIdenetifier).trim();
            entry = new AbstractMap.SimpleEntry<>(key, value);
        } else {
            final String key = line.trim();
            entry = new AbstractMap.SimpleEntry<>(key, null);
        }
        return entry;
    }
}
