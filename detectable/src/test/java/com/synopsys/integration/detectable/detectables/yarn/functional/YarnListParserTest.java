package com.synopsys.integration.detectable.detectables.yarn.functional;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLineLevelParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnListNode;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnListParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLock;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnTransformer;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;
import com.synopsys.integration.detectable.util.graph.GraphAssert;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

@FunctionalTest
public class YarnListParserTest {
    @Test
    void parseCrazyYarnListTest() {
        List<String> yarnLock = FunctionalTestFiles.asListOfStrings("/yarn/yarn-lock-missing.txt");
        List<String> yarnList = FunctionalTestFiles.asListOfStrings("/yarn/yarn-list-missing.txt");

        DependencyGraph dependencyGraph = createDependencyGraph(yarnLock, yarnList);
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasDependency("missing", "1.0.0");
    }

    @Test
    void parseYarnListTest() {
        List<String> yarnLock = FunctionalTestFiles.asListOfStrings("/yarn/yarn-lock-simple.txt");
        List<String> yarnList = FunctionalTestFiles.asListOfStrings("/yarn/yarn-list-simple.txt");

        DependencyGraph dependencyGraph = createDependencyGraph(yarnLock, yarnList);
        GraphCompare.assertEqualsResource("/yarn/yarn-simple-expected-graph.json", dependencyGraph);
    }

    @Test
    void parseYarnListWithResolvableVersions() {
        List<String> yarnLock = FunctionalTestFiles.asListOfStrings("/yarn/yarn-lock-resolved.txt");
        List<String> yarnList = FunctionalTestFiles.asListOfStrings("/yarn/yarn-list-resolved.txt");

        final DependencyGraph dependencyGraph = createDependencyGraph(yarnLock, yarnList);
        GraphCompare.assertEqualsResource("/yarn/yarn-resolved-expected-graph.json", dependencyGraph);
    }

    private DependencyGraph createDependencyGraph(List<String> yarnLockText, List<String> yarnListText) {
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
