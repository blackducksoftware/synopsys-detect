package com.synopsys.integration.detectable.detectables.bitbake.unit;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.util.graph.GraphAssert;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

@UnitTest
public class BitbakeGraphTransformerTest {
    @Test
    public void parentHasChild() {
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final BitbakeGraph bitbakeGraph = new BitbakeGraph();
        bitbakeGraph.addNode("example", "1:75-r50");
        bitbakeGraph.addNode("foobar", "12");
        bitbakeGraph.addChild("example", "foobar");

        final Map<String, String> recipeToLayerMap = new HashMap<>();
        recipeToLayerMap.put("example", "meta");
        recipeToLayerMap.put("foobar", "meta");

        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, recipeToLayerMap);

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.YOCTO, dependencyGraph);

        final ExternalId example = graphAssert.hasDependency(externalIdFactory.createYoctoExternalId("meta", "example", "1:75-r50"));
        final ExternalId foobar = graphAssert.hasDependency(externalIdFactory.createYoctoExternalId("meta", "foobar", "12"));
        graphAssert.hasParentChildRelationship(example, foobar);
    }

    @Test
    public void ignoredNoVersionRelationship() {
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final BitbakeGraph bitbakeGraph = new BitbakeGraph();
        bitbakeGraph.addNode("example", "75");
        bitbakeGraph.addNode("foobar", null);
        bitbakeGraph.addChild("example", "foobar");

        final Map<String, String> recipeToLayerMap = new HashMap<>();
        recipeToLayerMap.put("example", "meta");
        recipeToLayerMap.put("foobar", "meta");

        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, recipeToLayerMap);

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.YOCTO, dependencyGraph);
        graphAssert.hasRootSize(1);
        final ExternalId externalId = graphAssert.hasDependency(externalIdFactory.createYoctoExternalId("meta", "example", "75"));
        graphAssert.hasRelationshipCount(externalId, 0);
    }

    @Test
    public void ignoredNoVersion() {
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final BitbakeGraph bitbakeGraph = new BitbakeGraph();
        bitbakeGraph.addNode("example", null);

        final Map<String, String> recipeToLayerMap = new HashMap<>();
        recipeToLayerMap.put("example", "meta");

        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, recipeToLayerMap);

        final GraphAssert graphAssert = new GraphAssert(Forge.YOCTO, dependencyGraph);
        graphAssert.hasNoDependency(externalIdFactory.createYoctoExternalId("meta", "example", null));
        graphAssert.hasRootSize(0);
    }
}
