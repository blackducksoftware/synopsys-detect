package com.synopsys.integration.detectable.detectables.bitbake.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphNodeLabelParser;

public class GraphNodeLabelParserTest {

    @Test
    void testVersion() {
        String labelValue = "acl-native do_compile\\n:2.3.1-r0\\nvirtual:native:/workdir/poky/meta/recipes-support/attr/acl_2.3.1.bb";
        GraphNodeLabelParser parser = new GraphNodeLabelParser();

        Optional<String> version = parser.parseVersionFromLabel(labelValue);

        assertTrue(version.isPresent());
        assertEquals("2.3.1-r0", version.get());
    }

    @Test
    void testLayer() {
        String labelValue = "acl-native do_compile\\n:2.3.1-r0\\nvirtual:native:/workdir/poky/meta/recipes-support/attr/acl_2.3.1.bb";
        GraphNodeLabelParser parser = new GraphNodeLabelParser();
        Set<String> knownLayers = new HashSet<>();
        knownLayers.add("meta");

        Optional<String> layer = parser.parseLayerFromLabel(labelValue, knownLayers);
        
        assertTrue(layer.isPresent());
        assertEquals("meta", layer.get());
    }
}
