package com.synopsys.integration.detectable.detectables.bitbake.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShowRecipesResults {
    private final Set<String> layerNames;
    private final Map<String, List<String>> recipesWithLayers;

    public ShowRecipesResults(Set<String> layerNames, Map<String, List<String>> recipesWithLayers) {
        this.layerNames = layerNames;
        this.recipesWithLayers = recipesWithLayers;
    }

    public Set<String> getLayerNames() {
        return layerNames;
    }

    public Map<String, List<String>> getRecipesWithLayers() {
        return recipesWithLayers;
    }
}
