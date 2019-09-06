package com.synopsys.integration.detectable.detectables.bitbake.functional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeRecipesParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

class BitbakeRecipesParserTest {

    @Test
    void parseComponentLayerMapTest() {
        final String output = FunctionalTestFiles.asString("/bitbake/bitbakeShowRecipesOutput.txt");
        final BitbakeRecipesParser bitbakeRecipesParser = new BitbakeRecipesParser();
        final Map<String, List<String>> componentLayerMap = bitbakeRecipesParser.parseComponentLayerMap(output);

        Assertions.assertEquals(Collections.singletonList("meta"), componentLayerMap.get("acl"));
        Assertions.assertEquals(Arrays.asList("meta", "meta-yocto-bsp"), componentLayerMap.get("acpica"));
        Assertions.assertEquals(Collections.singletonList("meta-poky"), componentLayerMap.get("acpid"));
        Assertions.assertEquals(Collections.singletonList("meta-yocto-bsp"), componentLayerMap.get("adwaita-icon-theme"));
        Assertions.assertEquals(Collections.singletonList("meta"), componentLayerMap.get("alsa-plugins"));
        Assertions.assertEquals(Collections.singletonList("meta"), componentLayerMap.get("alsa-utils"));
        Assertions.assertEquals(6, componentLayerMap.size());
    }
}