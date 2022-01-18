package com.synopsys.integration.detectable.detectables.bitbake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeRecipe;

public class BitbakeRecipesToLayerMapConverter {
    // TODO: do we ever need a list? Could we just build a map in the first place?
    public Map<String, List<String>> convert(List<BitbakeRecipe> bitbakeRecipes) {
        Map<String, List<String>> recipeNameToLayersMap = new HashMap<>();
        for (BitbakeRecipe bitbakeRecipe : bitbakeRecipes) {
            if (bitbakeRecipe.getLayerNames().size() > 0) {
                recipeNameToLayersMap.put(bitbakeRecipe.getName(), bitbakeRecipe.getLayerNames());
            }
        }
        return recipeNameToLayersMap;
    }
}
