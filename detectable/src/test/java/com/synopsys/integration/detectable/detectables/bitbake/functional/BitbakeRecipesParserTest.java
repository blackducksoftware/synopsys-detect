package com.synopsys.integration.detectable.detectables.bitbake.functional;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bitbake.model.RecipeLayerCatalog;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeRecipesParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

class BitbakeRecipesParserTest {
    @Test
    void parseComponentLayerMapTest() {
        final String output = FunctionalTestFiles.asString("/bitbake/bitbakeShowRecipesOutput.txt");
        final BitbakeRecipesParser bitbakeRecipesParser = new BitbakeRecipesParser();
        final RecipeLayerCatalog recipeLayerCatalog = bitbakeRecipesParser.parseComponentLayerMap(output);

        Assertions.assertEquals(Collections.singletonList("meta"), recipeLayerCatalog.getLayersForRecipe("acl"));
        Assertions.assertEquals(Arrays.asList("meta", "meta-yocto-bsp"), recipeLayerCatalog.getLayersForRecipe("acpica"));
        Assertions.assertEquals(Collections.singletonList("meta-poky"), recipeLayerCatalog.getLayersForRecipe("acpid"));
        Assertions.assertEquals(Collections.singletonList("meta-yocto-bsp"), recipeLayerCatalog.getLayersForRecipe("adwaita-icon-theme"));
        Assertions.assertEquals(Collections.singletonList("meta"), recipeLayerCatalog.getLayersForRecipe("alsa-plugins"));
        Assertions.assertEquals(Collections.singletonList("meta"), recipeLayerCatalog.getLayersForRecipe("alsa-utils"));
    }
}