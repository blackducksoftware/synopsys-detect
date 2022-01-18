package com.synopsys.integration.detectable.detectables.bitbake;

import java.util.List;
import java.util.Set;

import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeRecipe;

public class ShowRecipesResults {
    private final Set<String> layerNames;
    private final List<BitbakeRecipe> recipes;

    public ShowRecipesResults(final Set<String> layerNames, final List<BitbakeRecipe> recipes) {
        this.layerNames = layerNames;
        this.recipes = recipes;
    }

    public Set<String> getLayerNames() {
        return layerNames;
    }

    public List<BitbakeRecipe> getRecipes() {
        return recipes;
    }
}
