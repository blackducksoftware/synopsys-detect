package com.synopsys.integration.detectable.detectables.bitbake.data;

import java.util.List;

public class BitbakeRecipe {
    private final String name;
    private final List<String> layerNames;

    public BitbakeRecipe(String name, List<String> layerNames) {
        this.name = name;
        this.layerNames = layerNames;
    }

    public String getName() {
        return name;
    }

    public List<String> getLayerNames() {
        return layerNames;
    }

    public void addLayerName(String layer) {
        layerNames.add(layer);
    }
}
