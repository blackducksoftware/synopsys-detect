/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bitbake.model;

import java.util.List;

public class BitbakeRecipe {
    private final String name;
    private final List<String> layerNames;

    public BitbakeRecipe(final String name, final List<String> layerNames) {
        this.name = name;
        this.layerNames = layerNames;
    }

    public String getName() {
        return name;
    }

    public List<String> getLayerNames() {
        return layerNames;
    }

    public void addLayerName(final String layer) {
        layerNames.add(layer);
    }
}
