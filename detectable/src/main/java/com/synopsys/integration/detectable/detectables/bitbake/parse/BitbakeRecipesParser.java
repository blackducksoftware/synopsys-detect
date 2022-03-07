package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.bitbake.data.BitbakeRecipe;
import com.synopsys.integration.detectable.detectables.bitbake.data.ShowRecipesResults;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class BitbakeRecipesParser {
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    /**
     * @param showRecipeLines is the executable output.
     * @return Recipe names mapped to a recipe's the layer names.
     */
    public ShowRecipesResults parseShowRecipes(List<String> showRecipeLines) {
        Map<String, List<String>> bitbakeRecipes = new HashMap<>();
        Set<String> layerNames = new HashSet<>();

        boolean started = false;
        BitbakeRecipe currentRecipe = null;
        for (String line : showRecipeLines) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            if (!started && line.trim().startsWith("=== Available recipes: ===")) {
                started = true;
            } else if (started) {
                currentRecipe = parseLine(line, currentRecipe, bitbakeRecipes, layerNames);
            }
        }
        finishCurrentRecipe(bitbakeRecipes, layerNames, currentRecipe);
        return new ShowRecipesResults(layerNames, bitbakeRecipes);
    }

    private BitbakeRecipe parseLine(String line, BitbakeRecipe currentRecipe, Map<String, List<String>> bitbakeRecipes, Set<String> layerNames) {
        if (line.contains(":") && !line.startsWith("  ")) {
            finishCurrentRecipe(bitbakeRecipes, layerNames, currentRecipe);
            // Parse beginning of new component
            String recipeName = line.replace(":", "").trim();
            return new BitbakeRecipe(recipeName, new ArrayList<>());
        } else if (currentRecipe != null && line.startsWith("  ")) {
            // Parse the layer and version for the current component
            String trimmedLine = line.trim();
            int indexOfFirstSpace = trimmedLine.indexOf(' ');
            int indexOfLastSpace = trimmedLine.lastIndexOf(' ');

            if (indexOfFirstSpace != -1 && indexOfLastSpace != -1 && indexOfLastSpace + 1 < trimmedLine.length()) {
                String layer = trimmedLine.substring(0, indexOfFirstSpace);
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

    private void finishCurrentRecipe(Map<String, List<String>> bitbakeRecipes, Set<String> layerNames, @Nullable BitbakeRecipe currentRecipe) {
        if (currentRecipe != null) {
            bitbakeRecipes.put(currentRecipe.getName(), currentRecipe.getLayerNames());
            if (currentRecipe.getLayerNames() != null) {
                layerNames.addAll(currentRecipe.getLayerNames());
            }
        }
    }
}
