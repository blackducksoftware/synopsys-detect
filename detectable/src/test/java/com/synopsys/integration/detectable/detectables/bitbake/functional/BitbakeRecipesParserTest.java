package com.synopsys.integration.detectable.detectables.bitbake.functional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeRecipe;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeRecipesParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

class BitbakeRecipesParserTest {

    @Test
    void parseComponentLayerMapTest() {
        final String output = FunctionalTestFiles.asString("/bitbake/bitbakeShowRecipesOutput.txt");
        final BitbakeRecipesParser bitbakeRecipesParser = new BitbakeRecipesParser();
        final Map<String, BitbakeRecipe> componentLayerMap = bitbakeRecipesParser.parseComponentLayerMap(output);

        Assertions.assertEquals(Collections.singletonList("meta"), getLayers(componentLayerMap, "acl"));
        Assertions.assertEquals(Arrays.asList("meta", "meta-yocto-bsp"), getLayers(componentLayerMap, "acpica"));
        Assertions.assertEquals(Collections.singletonList("meta-poky"), getLayers(componentLayerMap, "acpid"));
        Assertions.assertEquals(Collections.singletonList("meta-yocto-bsp"), getLayers(componentLayerMap, "adwaita-icon-theme"));
        Assertions.assertEquals(Collections.singletonList("meta"), getLayers(componentLayerMap, "alsa-plugins"));
        Assertions.assertEquals(Collections.singletonList("meta"), getLayers(componentLayerMap, "alsa-utils"));
        Assertions.assertEquals(6, componentLayerMap.size());
    }

    private List<String> getLayers(final Map<String, BitbakeRecipe> componentLayerMap, final String componentName) {
        return componentLayerMap.get(componentName).getLayers().stream()
                   .map(BitbakeRecipe.Layer::getLayerName)
                   .collect(Collectors.toList());
    }
}