package com.synopsys.integration.detectable.detectables.bitbake.unit;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.util.GraphAssert;

@UnitTest
public class BitbakeGraphTransformerTest {
    @Test
    public void parentHasChild() {
        BitbakeGraph bitbakeGraph = new BitbakeGraph();
        bitbakeGraph.addNode("example", Optional.of("75"));
        bitbakeGraph.addNode("foobar", Optional.of("12"));
        bitbakeGraph.addChild("example", "foobar");

        BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, "archy");

        GraphAssert graphAssert = new GraphAssert(Forge.YOCTO, dependencyGraph);

        ExternalId foobar = graphAssert.hasDependency("foobar", "12", "archy");
        ExternalId example = graphAssert.hasDependency("example", "75", "archy");
        graphAssert.hasParentChildRelationship(example, foobar);
    }

    @Test
    public void ignoredNoVersionRelationship() {
        BitbakeGraph bitbakeGraph = new BitbakeGraph();
        bitbakeGraph.addNode("example", Optional.of("75"));
        bitbakeGraph.addNode("foobar", Optional.empty());
        bitbakeGraph.addChild("example", "foobar");

        BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, "archy");

        GraphAssert graphAssert = new GraphAssert(Forge.YOCTO, dependencyGraph);
        graphAssert.rootSize(1);
        ExternalId externalId = graphAssert.hasDependency("example", "75", "archy");
        graphAssert.relationshipCount(externalId, 0);
    }

    @Test
    public void ignoredNoVersion() {
        BitbakeGraph bitbakeGraph = new BitbakeGraph();
        bitbakeGraph.addNode("example", Optional.empty());

        BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, "archy");

        GraphAssert graphAssert = new GraphAssert(Forge.YOCTO, dependencyGraph);
        graphAssert.noDependency("example", null, "archy");
        graphAssert.rootSize(0);
    }
}
