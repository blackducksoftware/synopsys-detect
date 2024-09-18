package com.blackduck.integration.detectable.detectables.conan.unit.cli.parser.conan2.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.blackduck.integration.detectable.detectables.conan.cli.parser.conan2.model.ConanGraphInfoDependency;
import com.blackduck.integration.detectable.detectables.conan.cli.parser.conan2.model.ConanGraphInfoGraphNode;

public class ConanGraphInfoGraphNodeTest {
    @Test
    public void testGenerateExternalIdVersion() {
        ConanGraphInfoGraphNode node = new ConanGraphInfoGraphNode(
            "name1",
            "version1",
            "recipeRevision1",
            "packageRevision1",
            "packageId1",
            null,
            null,
            null
        );

        assertEquals(
            "version1@_/_#recipeRevision1",
            node.generateExternalIdVersion(false)
        );
    }

    @Test
    public void testGenerateExternalIdVersion_channelAndUserAreSet() {
        ConanGraphInfoGraphNode node = new ConanGraphInfoGraphNode(
            "name1",
            "version1",
            "recipeRevision1",
            "packageRevision1",
            "packageId1",
            "channel1",
            "user1",
            null
        );

        assertEquals(
            "version1@channel1/user1#recipeRevision1",
            node.generateExternalIdVersion(false)
        );
    }

    @Test
    public void testGenerateExternalIdVersion_preferLong() {
        ConanGraphInfoGraphNode node = new ConanGraphInfoGraphNode(
            "name1",
            "version1",
            "recipeRevision1",
            "packageRevision1",
            "packageId1",
            null,
            null,
            null
        );

        assertEquals(
            "version1@_/_#recipeRevision1:packageId1#packageRevision1",
            node.generateExternalIdVersion(true)
        );
    }

    @Test
    public void testGenerateExternalIdVersion_preferLong_packageIdIsNull() {
        ConanGraphInfoGraphNode node = new ConanGraphInfoGraphNode(
            "name1",
            "version1",
            "recipeRevision1",
            "packageRevision1",
            null,
            null,
            null,
            null
        );

        assertEquals(
            "version1@_/_#recipeRevision1:0#packageRevision1",
            node.generateExternalIdVersion(true)
        );
    }

    @Test
    public void testGetDirectDependencyIndecesIncludeBuild() {
        ConanGraphInfoGraphNode node = makeGraphNodeWithDependencies();

        Integer[] expected = {0, 1};
        Integer[] actual = node.getDirectDependencyIndeces(true).stream().toArray(Integer[] ::new);
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetDirectDependencyIndecesExcludeBuild() {
        ConanGraphInfoGraphNode node = makeGraphNodeWithDependencies();

        Integer[] expected = {1};
        Integer[] actual = node.getDirectDependencyIndeces(false).stream().toArray(Integer[] ::new);

        assertArrayEquals(expected, actual);
    }

    private ConanGraphInfoGraphNode makeGraphNodeWithDependencies() {
        Map<Integer, ConanGraphInfoDependency> dependencies = new HashMap<>();

        dependencies.put(0, new ConanGraphInfoDependency(true, true));
        dependencies.put(1, new ConanGraphInfoDependency(true, false));
        dependencies.put(2, new ConanGraphInfoDependency(false, true));
        dependencies.put(3, new ConanGraphInfoDependency(false, false));

        return new ConanGraphInfoGraphNode(null, null, null, null, null, null, null, dependencies);
    }
}
