/*
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
package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeRecipe;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class BitbakeRecipesParser {
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    /**
     * @param showRecipeLines is the executable output.
     * @return Recipe names mapped to a recipe's the layer names.
     */
    public List<BitbakeRecipe> parseShowRecipes(final List<String> showRecipeLines) {
        final List<BitbakeRecipe> bitbakeRecipes = new ArrayList<>();

        boolean started = false;
        BitbakeRecipe currentRecipe = null;
        for (final String line : showRecipeLines) {
            if (StringUtils.isBlank(line)) {
                continue;
            }

            if (!started && line.trim().startsWith("=== Available recipes: ===")) {
                started = true;
            } else if (started) {
                currentRecipe = parseLine(line, currentRecipe, bitbakeRecipes);
            }
        }

        if (currentRecipe != null) {
            bitbakeRecipes.add(currentRecipe);
        }

        return bitbakeRecipes;
    }

    private BitbakeRecipe parseLine(final String line, final BitbakeRecipe currentRecipe, final List<BitbakeRecipe> bitbakeRecipes) {
        if (line.contains(":") && !line.startsWith("  ")) {
            // Parse beginning of new component
            if (currentRecipe != null) {
                bitbakeRecipes.add(currentRecipe);
            }

            final String recipeName = line.replace(":", "").trim();
            return new BitbakeRecipe(recipeName, new ArrayList<>());
        } else if (currentRecipe != null && line.startsWith("  ")) {
            // Parse the layer and version for the current component
            final String trimmedLine = line.trim();
            final int indexOfFirstSpace = trimmedLine.indexOf(' ');
            final int indexOfLastSpace = trimmedLine.lastIndexOf(' ');

            if (indexOfFirstSpace != -1 && indexOfLastSpace != -1 && indexOfLastSpace + 1 < trimmedLine.length()) {
                final String layer = trimmedLine.substring(0, indexOfFirstSpace);
                currentRecipe.addLayerName(layer);
            } else {
                logger.debug(String.format("Failed to parse layer for component '%s' from line '%s'.", currentRecipe.getName(), line));
            }

            return currentRecipe;
        } else {
            logger.debug(String.format("Failed to parse line '%s'.", line));
            return currentRecipe;
        }
    }
}
