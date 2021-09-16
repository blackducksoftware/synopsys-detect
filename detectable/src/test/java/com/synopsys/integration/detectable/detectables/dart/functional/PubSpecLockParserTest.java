package com.synopsys.integration.detectable.detectables.dart.functional;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.dart.pubspec.PubSpecLockParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class PubSpecLockParserTest {
    @Test
    public void test() {
        List<String> pubSpecLockLines = FunctionalTestFiles.asListOfStrings("/dart/pubspec.lock");

        PubSpecLockParser pubSpecLockParser = new PubSpecLockParser(new ExternalIdFactory());

        DependencyGraph dependencyGraph = pubSpecLockParser.parse(pubSpecLockLines);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.DART, dependencyGraph);

        graphAssert.hasRootSize(3);
        graphAssert.hasRootDependency("async", "2.6.1");
        graphAssert.hasRootDependency("boolean_selector", "2.1.0");
        graphAssert.hasRootDependency("characters", "1.1.0");
    }
}
