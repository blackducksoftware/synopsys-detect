package com.synopsys.integration.detectable.detectables.bitbake.functional;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.detectables.bitbake.parse.RecipeDependsGraphParserTransformer;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.ArchitectureGraphAssert;

@FunctionalTest
public class BitbakeDependencyGraphFunctionalTest {
    @Test
    public void found480RootInOutput() throws IOException {
        final RecipeDependsGraphParserTransformer recipeDependsGraphParserTransformer = new RecipeDependsGraphParserTransformer();
        final InputStream inputStream = FunctionalTestFiles.asInputStream("/bitbake/Bitbake_RecipeDepends_Full.dot");
        final GraphParser graphParser = new GraphParser(inputStream);
        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());

        final BitbakeGraph bitbakeGraph = recipeDependsGraphParserTransformer.transform(graphParser);
        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, "i586-poky-linux");

        assert dependencyGraph.getRootDependencies().size() == 480;
    }

    @Test
    public void foundAttrAndAcl() throws IOException {
        final RecipeDependsGraphParserTransformer recipeDependsGraphParserTransformer = new RecipeDependsGraphParserTransformer();
        final InputStream inputStream = FunctionalTestFiles.asInputStream("/bitbake/Bitbake_RecipeDepends_Simple.dot");
        final GraphParser graphParser = new GraphParser(inputStream);
        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        final BitbakeGraph bitbakeGraph = recipeDependsGraphParserTransformer.transform(graphParser);
        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, "i586-poky-linux");

        final ArchitectureGraphAssert graphAssert = new ArchitectureGraphAssert(Forge.YOCTO, dependencyGraph);
        final ExternalId attr = graphAssert.hasDependency("attr", "2.4.47-r0", "i586-poky-linux");
        final ExternalId acl = graphAssert.hasDependency("acl", "2.2.52-r0", "i586-poky-linux");
        graphAssert.hasParentChildRelationship(acl, attr);
        graphAssert.hasRootSize(2);
    }
}
