/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class BitbakeRecipesParser {
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    /**
     * @param output is the executable output.
     * @return Component names mapped to a list of layer names.
     */
    public Map<String, List<String>> parseComponentLayerMap(final String output) {
        final Map<String, List<String>> componentLayerMap = new HashMap<>();

        boolean started = false;
        String currentComponentName = null;
        List<String> currentLayers = null;
        for (final String line : output.split(System.lineSeparator())) {
            if (StringUtils.isBlank(line)) {
                continue;
            } else if (!started && line.trim().startsWith("=== Available recipes: ===")) {
                started = true;
                continue;
            } else if (!started) {
                continue;
            }

            if (line.contains(":") && !line.startsWith("  ")) {
                if (currentComponentName != null) {
                    componentLayerMap.put(currentComponentName.trim(), currentLayers);
                }

                currentComponentName = line.replace(":", "").trim();
                currentLayers = new ArrayList<>();
            } else if (currentComponentName != null && line.startsWith("  ")) {
                final String trimmedLine = line.trim();
                final int indexOfFirstSpace = trimmedLine.indexOf(" ");

                if (indexOfFirstSpace != -1) {
                    final String layer = trimmedLine.substring(0, indexOfFirstSpace);
                    currentLayers.add(layer);
                } else {
                    logger.debug(String.format("Failed to parse layer for component '%s' from line '%s'.", currentComponentName, line));
                }
            } else {
                currentComponentName = null;
                currentLayers = new ArrayList<>();
                logger.debug(String.format("Failed to parse line '%s'.", line));
            }
        }

        if (currentComponentName != null) {
            componentLayerMap.put(currentComponentName.trim(), currentLayers);
        }

        return componentLayerMap;
    }
}
