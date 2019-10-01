package com.synopsys.integration.detectable.detectables.bitbake.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RecipeLayerCatalog {
    private final Map<String, List<String>> recipeNameToLayersMap;

    public RecipeLayerCatalog(final Map<String, List<String>> recipeNameToLayersMap) {
        this.recipeNameToLayersMap = recipeNameToLayersMap;
    }

    public void addRecipe(final String recipeName, final List<String> layerNames) {
        recipeNameToLayersMap.put(recipeName, layerNames);
    }

    /**
     * @param recipeName
     * @return the highest priority layer name for the given recipe.
     */
    public Optional<String> getPriorityLayerForRecipe(final String recipeName) {
        return getLayersForRecipe(recipeName).stream().findFirst();
    }

    public List<String> getLayersForRecipe(final String recipeName) {
        List<String> layers = recipeNameToLayersMap.get(recipeName);
        if (layers == null) {
            layers = Collections.emptyList();
        }

        return layers;
    }
}
