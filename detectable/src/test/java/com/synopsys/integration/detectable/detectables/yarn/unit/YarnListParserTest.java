package com.synopsys.integration.detectable.detectables.yarn.unit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLineLevelParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnListNode;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnListParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLock;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnTransformer;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

@UnitTest
public class YarnListParserTest {
    @Test
    void testCapitalNamePreserverd() {
        final List<String> testLines = new ArrayList<>();
        testLines.add("yarn list v1.5.1");
        testLines.add("├─ SOMEcapital@1.0.4");

        YarnListParser yarnListParser = new YarnListParser(new YarnLineLevelParser());
        List<YarnListNode> result = yarnListParser.parseYarnList(testLines);

        assertEquals(1, result.size());
        assertEquals("SOMEcapital", result.get(0).getPackageName());
    }

    @Test
    void testDependencyInYarnListAndNotInLock() {
        final List<String> designedYarnLock = new ArrayList<>();
        designedYarnLock.add("ajv@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");

        final List<String> testLines = new ArrayList<>();
        testLines.add("yarn list v1.5.1");
        testLines.add("├─ abab@1.0.4");

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        DependencyGraph dependencyGraph = createDependencyGraph(designedYarnLock, testLines);

        final GraphAssert graphAssert = new GraphAssert(Forge.NPM, dependencyGraph);
        graphAssert.hasRootDependency(externalIdFactory.createNameVersionExternalId(Forge.NPM, "abab", "1.0.4"));

        final int rootDependencyCount = dependencyGraph.getRootDependencyExternalIds().size();
        assertEquals(1, rootDependencyCount);
    }

    @Test
    void testThatYarnListLineAtBeginningIsIgnored() {
        final List<String> designedYarnLock = new ArrayList<>();
        designedYarnLock.add("abab@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");

        final List<String> testLines = new ArrayList<>();
        testLines.add("yarn list v1.5.1");
        testLines.add("├─ abab@1.0.4");

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        DependencyGraph dependencyGraph = createDependencyGraph(designedYarnLock, testLines);

        final GraphAssert graphAssert = new GraphAssert(Forge.NPM, dependencyGraph);
        graphAssert.hasRootDependency(externalIdFactory.createNameVersionExternalId(Forge.NPM, "abab", "1.0.4"));

        final int rootDependencySize = dependencyGraph.getRootDependencyExternalIds().size();
        assertEquals(1, rootDependencySize);
    }

    @Test
    void testThatYarnListWithOnlyTopLevelDependenciesIsParsedCorrectly() {
        final List<String> designedYarnLock = new ArrayList<>();
        designedYarnLock.add("esprima@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");
        designedYarnLock.add("extsprintf@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");

        final List<String> testLines = new ArrayList<>();
        testLines.add("├─ esprima@3.1.3");
        testLines.add("└─ extsprintf@1.3.0");

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        DependencyGraph dependencyGraph = createDependencyGraph(designedYarnLock, testLines);

        final GraphAssert graphAssert = new GraphAssert(Forge.NPM, dependencyGraph);
        graphAssert.hasDependency(externalIdFactory.createNameVersionExternalId(Forge.NPM, "esprima", "3.1.3"));
        graphAssert.hasDependency(externalIdFactory.createNameVersionExternalId(Forge.NPM, "extsprintf", "1.3.0"));
    }

    @Test
    void testThatYarnListWithGrandchildIsParsedCorrectly() {
        final List<String> designedYarnLock = new ArrayList<>();
        designedYarnLock.add("yargs-parse@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");
        designedYarnLock.add("camelcase@^3.0.0:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");

        final List<String> testLines = new ArrayList<>();
        testLines.add("├─ yargs-parse@4.2.1");
        testLines.add("│  └─ camelcase@^3.0.0");

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        DependencyGraph dependencyGraph = createDependencyGraph(designedYarnLock, testLines);

        final GraphAssert graphAssert = new GraphAssert(Forge.NPM, dependencyGraph);
        final ExternalId rootDependency = graphAssert.hasDependency(externalIdFactory.createNameVersionExternalId(Forge.NPM, "yargs-parse", "4.2.1"));
        graphAssert.hasParentChildRelationship(rootDependency, externalIdFactory.createNameVersionExternalId(Forge.NPM, "camelcase", "5.5.2"));
    }

    @Test
    void testThatYarnListWithGreatGrandchildrenIsParsedCorrectly() {
        final List<String> designedYarnLock = new ArrayList<>();
        designedYarnLock.add("yargs-parse@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");
        designedYarnLock.add("camelcase@^3.0.0:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");
        designedYarnLock.add("ms@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");

        final List<String> testLines = new ArrayList<>();
        testLines.add("├─ yargs-parse@4.2.1");
        testLines.add("│  └─ camelcase@^3.0.0");
        testLines.add("│  │  └─ ms@0.7.2");

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        DependencyGraph dependencyGraph = createDependencyGraph(designedYarnLock, testLines);

        final GraphAssert graphAssert = new GraphAssert(Forge.NPM, dependencyGraph);
        final ExternalId rootDependency = graphAssert.hasDependency(externalIdFactory.createNameVersionExternalId(Forge.NPM, "yargs-parse", "4.2.1"));
        final ExternalId childDependency = graphAssert.hasParentChildRelationship(rootDependency, externalIdFactory.createNameVersionExternalId(Forge.NPM, "camelcase", "5.5.2"));
        graphAssert.hasParentChildRelationship(childDependency, externalIdFactory.createNameVersionExternalId(Forge.NPM, "ms", "0.7.2"));
    }

    private DependencyGraph createDependencyGraph(List<String> yarnLockText, List<String> yarnListText){
        final YarnLineLevelParser lineLevelParser = new YarnLineLevelParser();
        final YarnLockParser yarnLockParser = new YarnLockParser(lineLevelParser);
        final YarnListParser yarnListParser = new YarnListParser(lineLevelParser);

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final YarnTransformer yarnTransformer = new YarnTransformer(externalIdFactory);

        YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockText);
        List<YarnListNode> yarnList = yarnListParser.parseYarnList(yarnListText);
        final DependencyGraph dependencyGraph = yarnTransformer.transform(yarnList, yarnLock);
        return dependencyGraph;
    }
}
