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

import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeRecipe;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class BitbakeRecipesParser {
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    /**
     * @param output is the executable output.
     * @return Component names mapped to a list of layer names.
     */
    public Map<String, BitbakeRecipe> parseComponentLayerMap(final String output) {
        final Map<String, BitbakeRecipe> componentLayerMap = new HashMap<>();

        boolean started = false;
        String currentComponentName = null;
        List<BitbakeRecipe.Layer> currentLayers = null;
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
                    final BitbakeRecipe bitbakeRecipe = new BitbakeRecipe(currentComponentName, currentLayers);
                    componentLayerMap.put(currentComponentName.trim(), bitbakeRecipe);
                }

                currentComponentName = line.replace(":", "").trim();
                currentLayers = new ArrayList<>();
            } else if (currentComponentName != null && line.startsWith("  ")) {
                final String trimmedLine = line.trim();
                final int indexOfFirstSpace = trimmedLine.indexOf(" ");
                final int indexOfLastSpace = trimmedLine.lastIndexOf(" ");

                if (indexOfFirstSpace != -1 && indexOfLastSpace != -1 && indexOfLastSpace + 1 < trimmedLine.length()) {
                    final String layer = trimmedLine.substring(0, indexOfFirstSpace);
                    final String version = trimmedLine.substring(indexOfLastSpace + 1);
                    currentLayers.add(new BitbakeRecipe.Layer(layer, version));
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
            final BitbakeRecipe bitbakeRecipe = new BitbakeRecipe(currentComponentName, currentLayers);
            componentLayerMap.put(currentComponentName.trim(), bitbakeRecipe);
        }

        return componentLayerMap;
    }
}
