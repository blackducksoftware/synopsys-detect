package com.synopsys.integration.detectable.detectables.bitbake.functional;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeLayersParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

class BitbakeLayersParserTest {

    @Test
    void parseTest() {
        final String bitbakeLayersOutput = FunctionalTestFiles.asString("/bitbake/bitbakeShowLayersOutput.txt");
        final BitbakeLayersParser bitbakeLayersParser = new BitbakeLayersParser();
        final Map<String, Integer> layerPriorityMap = bitbakeLayersParser.parseLayerPriorityMap(bitbakeLayersOutput);

        Assertions.assertEquals(new Integer(5), layerPriorityMap.get("meta"));
        Assertions.assertEquals(new Integer(5), layerPriorityMap.get("meta-poky"));
        Assertions.assertEquals(new Integer(6), layerPriorityMap.get("meta-yocto-bsp"));
        Assertions.assertEquals(3, layerPriorityMap.size());
    }
}