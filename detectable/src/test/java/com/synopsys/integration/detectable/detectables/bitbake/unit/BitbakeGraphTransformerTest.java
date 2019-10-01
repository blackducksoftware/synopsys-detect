package com.synopsys.integration.detectable.detectables.bitbake.unit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.model.RecipeLayerCatalog;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.util.graph.GraphAssert;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

@UnitTest
public class BitbakeGraphTransformerTest {
    @Test
    public void parentHasChild() {
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final BitbakeGraph bitbakeGraph = new BitbakeGraph();
        bitbakeGraph.addNode("example", Optional.of("75"));
        bitbakeGraph.addNode("foobar", Optional.of("12"));
        bitbakeGraph.addChild("example", "foobar");

        final RecipeLayerCatalog recipeLayerCatalog = new RecipeLayerCatalog(new HashMap<>());
        recipeLayerCatalog.addRecipe("example", Arrays.asList("meta", "bad-layer"));
        recipeLayerCatalog.addRecipe("foobar", Arrays.asList("meta", "bad-layer"));

        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, recipeLayerCatalog);

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.YOCTO, dependencyGraph);

        final ExternalId example = graphAssert.hasDependency(externalIdFactory.createYoctoExternalId("meta", "example", "75"));
        final ExternalId foobar = graphAssert.hasDependency(externalIdFactory.createYoctoExternalId("meta", "foobar", "12"));
        graphAssert.hasParentChildRelationship(example, foobar);
    }

    @Test
    public void ignoredNoVersionRelationship() {
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final BitbakeGraph bitbakeGraph = new BitbakeGraph();
        bitbakeGraph.addNode("example", Optional.of("75"));
        bitbakeGraph.addNode("foobar", Optional.empty());
        bitbakeGraph.addChild("example", "foobar");

        final RecipeLayerCatalog recipeLayerCatalog = new RecipeLayerCatalog(new HashMap<>());
        recipeLayerCatalog.addRecipe("example", Arrays.asList("meta", "bad-layer"));
        recipeLayerCatalog.addRecipe("foobar", Arrays.asList("meta", "bad-layer"));

        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, recipeLayerCatalog);

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.YOCTO, dependencyGraph);
        graphAssert.hasRootSize(1);
        final ExternalId externalId = graphAssert.hasDependency(externalIdFactory.createYoctoExternalId("meta", "example", "75"));
        graphAssert.hasRelationshipCount(externalId, 0);
    }

    @Test
    public void ignoredNoVersion() {
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final BitbakeGraph bitbakeGraph = new BitbakeGraph();
        bitbakeGraph.addNode("example", Optional.empty());

        final RecipeLayerCatalog recipeLayerCatalog = new RecipeLayerCatalog(new HashMap<>());
        recipeLayerCatalog.addRecipe("example", Arrays.asList("meta", "bad-layer"));

        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, recipeLayerCatalog);

        final GraphAssert graphAssert = new GraphAssert(Forge.YOCTO, dependencyGraph);
        graphAssert.hasNoDependency(externalIdFactory.createModuleNamesExternalId(Forge.YOCTO, "meta", "example", null));
        graphAssert.hasRootSize(0);
    }
}
