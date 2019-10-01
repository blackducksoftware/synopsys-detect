package com.synopsys.integration.detectable.detectables.bitbake.functional;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

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
import com.synopsys.integration.detectable.detectables.bitbake.model.RecipeLayerCatalog;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeRecipesParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphParserTransformer;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

@FunctionalTest
public class BitbakeDependencyGraphFunctionalTest {
    @Test
    public void found176RootInOutput() {
        final GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        final InputStream inputStream = FunctionalTestFiles.asInputStream("/bitbake/Bitbake_RecipeDepends_Full.dot");
        final GraphParser graphParser = new GraphParser(inputStream);
        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        final BitbakeGraph bitbakeGraph = graphParserTransformer.transform(graphParser, BitbakeFileType.RECIPE_DEPENDS);

        final BitbakeRecipesParser bitbakeRecipesParser = new BitbakeRecipesParser();
        final String recipeOutput = FunctionalTestFiles.asString("/bitbake/bitbakeShowRecipesFull_recipe.txt");
        final RecipeLayerCatalog recipeLayerCatalog = bitbakeRecipesParser.parseComponentLayerMap(recipeOutput);

        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, recipeLayerCatalog);

        Assertions.assertEquals(176, dependencyGraph.getRootDependencies().size());
    }

    @Test
    public void foundAttrAndAcl() {
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        final InputStream inputStream = FunctionalTestFiles.asInputStream("/bitbake/Bitbake_RecipeDepends_Simple.dot");
        final GraphParser graphParser = new GraphParser(inputStream);
        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        final BitbakeGraph bitbakeGraph = graphParserTransformer.transform(graphParser, BitbakeFileType.RECIPE_DEPENDS);

        final RecipeLayerCatalog recipeLayerCatalog = new RecipeLayerCatalog(new HashMap<>());
        recipeLayerCatalog.addRecipe("acl", Arrays.asList("meta", "bad-layer"));
        recipeLayerCatalog.addRecipe("attr", Arrays.asList("meta", "bad-layer"));

        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, recipeLayerCatalog);

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.YOCTO, dependencyGraph);
        final ExternalId attr = graphAssert.hasDependency(externalIdFactory.createYoctoExternalId("meta", "attr", "2.4.47-r0"));
        final ExternalId acl = graphAssert.hasDependency(externalIdFactory.createYoctoExternalId("meta", "acl", "2.2.52-r0"));
        graphAssert.hasParentChildRelationship(acl, attr);
        graphAssert.hasRootSize(2);
    }

    @Test
    public void found151RootInOutputPackage() {
        final GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        final InputStream inputStream = FunctionalTestFiles.asInputStream("/bitbake/Bitbake_PackageDepends_Full.dot");
        final GraphParser graphParser = new GraphParser(inputStream);
        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());

        final BitbakeGraph bitbakeGraph = graphParserTransformer.transform(graphParser, BitbakeFileType.PACKAGE_DEPENDS);

        final BitbakeRecipesParser bitbakeRecipesParser = new BitbakeRecipesParser();
        final String recipeOutput = FunctionalTestFiles.asString("/bitbake/bitbakeShowRecipesFull_package.txt");
        final RecipeLayerCatalog recipeLayerCatalog = bitbakeRecipesParser.parseComponentLayerMap(recipeOutput);

        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, recipeLayerCatalog);

        Assertions.assertEquals(151, dependencyGraph.getRootDependencies().size());
    }

    @Test
    public void foundBusyboxAndShadowPackage() {
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        final InputStream inputStream = FunctionalTestFiles.asInputStream("/bitbake/Bitbake_PackageDepends_Simple.dot");
        final GraphParser graphParser = new GraphParser(inputStream);
        final BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        final BitbakeGraph bitbakeGraph = graphParserTransformer.transform(graphParser, BitbakeFileType.PACKAGE_DEPENDS);

        final RecipeLayerCatalog recipeLayerCatalog = new RecipeLayerCatalog(new HashMap<>());
        recipeLayerCatalog.addRecipe("busybox", Arrays.asList("meta", "bad-layer"));
        recipeLayerCatalog.addRecipe("shadow", Arrays.asList("meta", "bad-layer"));

        final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, recipeLayerCatalog);

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.YOCTO, dependencyGraph);
        final ExternalId busybox = graphAssert.hasDependency(externalIdFactory.createYoctoExternalId("meta", "busybox", "1.23.2-r0"));
        final ExternalId shadow = graphAssert.hasDependency(externalIdFactory.createYoctoExternalId("meta", "shadow", "4.2.1-r0"));
        graphAssert.hasParentChildRelationship(busybox, shadow);
        graphAssert.hasRootSize(2);
    }
}
