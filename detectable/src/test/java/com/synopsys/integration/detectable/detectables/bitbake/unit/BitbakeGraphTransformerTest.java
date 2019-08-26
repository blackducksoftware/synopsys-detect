package com.synopsys.integration.detectable.detectables.bitbake.unit;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

@UnitTest
public class BitbakeGraphTransformerTest {
    @Test
    public void parentHasChild() {
        final BitbakeGraph bitbakeGraph = new BitbakeGraph();
        bitbakeGraph.addNode("example", Optional.of("75"));
        bitbakeGraph.addNode("foobar", Optional.of("12"));
        bitbakeGraph.addChild("example", "foobar");

        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph);

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.YOCTO, dependencyGraph);

        final ExternalId foobar = graphAssert.hasDependency("foobar", "12");
        final ExternalId example = graphAssert.hasDependency("example", "75");
        graphAssert.hasParentChildRelationship(example, foobar);
    }

    @Test
    public void ignoredNoVersionRelationship() {
        final BitbakeGraph bitbakeGraph = new BitbakeGraph();
        bitbakeGraph.addNode("example", Optional.of("75"));
        bitbakeGraph.addNode("foobar", Optional.empty());
        bitbakeGraph.addChild("example", "foobar");

        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph);

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.YOCTO, dependencyGraph);
        graphAssert.hasRootSize(1);
        final ExternalId externalId = graphAssert.hasDependency("example", "75");
        graphAssert.hasRelationshipCount(externalId, 0);
    }

    @Test
    public void ignoredNoVersion() {
        final BitbakeGraph bitbakeGraph = new BitbakeGraph();
        bitbakeGraph.addNode("example", Optional.empty());

        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph);

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.YOCTO, dependencyGraph);
        graphAssert.noDependency("example", null);
        graphAssert.hasRootSize(0);
    }
}
