package com.synopsys.integration.detectable.detectables.bitbake.functional;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeRecipe;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeRecipesParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

class BitbakeRecipesParserTest {
    @Test
    void parseComponentLayerMapTest() {
        final List<String> output = FunctionalTestFiles.asListOfStrings("/bitbake/bitbakeShowRecipesOutput.txt");
        final BitbakeRecipesParser bitbakeRecipesParser = new BitbakeRecipesParser();
        final List<BitbakeRecipe> bitbakeRecipes = bitbakeRecipesParser.parseShowRecipes(output);

        assertComponentWithLayers(bitbakeRecipes.get(0), "acl", "meta");
        assertComponentWithLayers(bitbakeRecipes.get(1), "acpica", "meta", "meta-yocto-bsp");
        assertComponentWithLayers(bitbakeRecipes.get(2), "acpid", "meta-poky");
        assertComponentWithLayers(bitbakeRecipes.get(3), "adwaita-icon-theme", "meta-yocto-bsp");
        assertComponentWithLayers(bitbakeRecipes.get(4), "alsa-plugins", "meta");
        assertComponentWithLayers(bitbakeRecipes.get(5), "alsa-utils", "meta");
    }

    private void assertComponentWithLayers(final BitbakeRecipe bitbakeRecipe, final String componentName, final String... layers) {
        Assertions.assertEquals(componentName, bitbakeRecipe.getName());
        Assertions.assertEquals(Arrays.asList(layers), bitbakeRecipe.getLayerNames());
    }
}