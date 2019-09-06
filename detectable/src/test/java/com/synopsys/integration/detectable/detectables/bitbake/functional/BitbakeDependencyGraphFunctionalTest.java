package com.synopsys.integration.detectable.detectables.bitbake.functional;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeFileType;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphParserTransformer;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

@FunctionalTest
public class BitbakeDependencyGraphFunctionalTest {
    @Test
    public void found480RootInOutput() {
        final GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        final InputStream inputStream = FunctionalTestFiles.asInputStream("/bitbake/Bitbake_RecipeDepends_Full.dot");
        final GraphParser graphParser = new GraphParser(inputStream);
        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());

        final BitbakeGraph bitbakeGraph = graphParserTransformer.transform(graphParser, BitbakeFileType.RECIPE_DEPENDS);
        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, null, null); // TODO: Provide maps

        assert dependencyGraph.getRootDependencies().size() == 480;
    }

    @Test
    public void foundAttrAndAcl() {
        final GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        final InputStream inputStream = FunctionalTestFiles.asInputStream("/bitbake/Bitbake_RecipeDepends_Simple.dot");
        final GraphParser graphParser = new GraphParser(inputStream);
        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        final BitbakeGraph bitbakeGraph = graphParserTransformer.transform(graphParser, BitbakeFileType.RECIPE_DEPENDS);
        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, null, null); // TODO: Provide maps

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.YOCTO, dependencyGraph);
        final ExternalId attr = graphAssert.hasDependency("attr", "2.4.47-r0");
        final ExternalId acl = graphAssert.hasDependency("acl", "2.2.52-r0");
        graphAssert.hasParentChildRelationship(acl, attr);
        graphAssert.hasRootSize(2);
    }

    @Test
    public void found480RootInOutputPackage() {
        final GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        final InputStream inputStream = FunctionalTestFiles.asInputStream("/bitbake/Bitbake_PackageDepends_Full.dot");
        final GraphParser graphParser = new GraphParser(inputStream);
        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());

        final BitbakeGraph bitbakeGraph = graphParserTransformer.transform(graphParser, BitbakeFileType.PACKAGE_DEPENDS);
        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, null, null); // TODO: Provide maps

        Assertions.assertEquals(771, dependencyGraph.getRootDependencies().size());
    }

    @Test
    public void foundAttrAndAclPackage() {
        final GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        final InputStream inputStream = FunctionalTestFiles.asInputStream("/bitbake/Bitbake_PackageDepends_Simple.dot");
        final GraphParser graphParser = new GraphParser(inputStream);
        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        final BitbakeGraph bitbakeGraph = graphParserTransformer.transform(graphParser, BitbakeFileType.PACKAGE_DEPENDS);
        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, null, null); // TODO: Provide maps

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.YOCTO, dependencyGraph);
        final ExternalId attr = graphAssert.hasDependency("shadow-native", "4.2.1-r0");
        final ExternalId acl = graphAssert.hasDependency("busybox", "1.23.2-r0");
        graphAssert.hasParentChildRelationship(acl, attr);
        graphAssert.hasRootSize(2);
    }
}
