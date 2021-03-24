/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bitbake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeRecipe;

public class BitbakeRecipesToLayerMapConverter {
    public Map<String, String> convert(final List<BitbakeRecipe> bitbakeRecipes) {
        final Map<String, String> recipeNameToLayersMap = new HashMap<>();

        for (final BitbakeRecipe bitbakeRecipe : bitbakeRecipes) {
            final String key = bitbakeRecipe.getName();
            bitbakeRecipe.getLayerNames().stream().findFirst().ifPresent(layer -> recipeNameToLayersMap.put(key, layer));
        }

        return recipeNameToLayersMap;
    }
}
