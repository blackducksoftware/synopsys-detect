/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
