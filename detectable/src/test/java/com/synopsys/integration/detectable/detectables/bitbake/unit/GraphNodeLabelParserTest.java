package com.synopsys.integration.detectable.detectables.bitbake.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphNodeLabelParser;
import com.synopsys.integration.exception.IntegrationException;

public class GraphNodeLabelParserTest {

    @Test
    void testVersion() throws IntegrationException {
        String labelValue = "acl-native do_compile\\n:2.3.1-r0\\nvirtual:native:/workdir/poky/meta/recipes-support/attr/acl_2.3.1.bb";
        GraphNodeLabelParser parser = new GraphNodeLabelParser();
        Set<String> knownLayers = new HashSet<>();
        knownLayers.add("meta");

        String version = parser.parseVersionFromLabel(labelValue);

        assertEquals("2.3.1-r0", version);
    }

    @Test
    void testLayer() throws IntegrationException {
        String labelValue = "acl-native do_compile\\n:2.3.1-r0\\nvirtual:native:/workdir/poky/meta/recipes-support/attr/acl_2.3.1.bb";
        GraphNodeLabelParser parser = new GraphNodeLabelParser();
        Set<String> knownLayers = new HashSet<>();
        knownLayers.add("meta");

        String layer = parser.parseLayerFromLabel(labelValue, knownLayers);

        assertEquals("meta", layer);
    }
}
