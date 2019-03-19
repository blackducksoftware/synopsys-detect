package com.synopsys.integration.detectable.detectables.yarn.functional;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
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

@FunctionalTest
public class YarnListParserTest {

    @Test
    void parseYarnListTest() {
        final List<String> designedYarnLock = new ArrayList<>();
        designedYarnLock.add("async@~0.9.0:");
        designedYarnLock.add("  version \"0.9.2\"");
        designedYarnLock.add("  resolved \"http://nexus/nexus3/repository/npm-all/async/-/async-0.9.2.tgz#aea74d5e61c1f899613bf64bda66d4c78f2fd17d\"");
        designedYarnLock.add("  dependencies:");
        designedYarnLock.add("    minimist \"0.0.8\"");
        designedYarnLock.add("");
        designedYarnLock.add("minimist@0.0.8:");
        designedYarnLock.add("  version \"0.0.8\"");
        designedYarnLock.add("  resolved \"http://nexus/nexus3/repository/npm-all/minimist/-/minimist-0.0.8.tgz#857fcabfc3397d2625b8228262e86aa7a011b05d\"");

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final List<String> yarnListText = FunctionalTestFiles.asListOfStrings("/yarn/yarn-list.txt");
        DependencyGraph dependencyGraph = createDependencyGraph(designedYarnLock, yarnListText);
        GraphCompare.assertEqualsResource("/yarn/list-expected-graph.json", dependencyGraph);
    }

    @Test
    void parseYarnListWithResolvableVersions() {
        final List<String> designedYarnLock = new ArrayList<>();
        designedYarnLock.add("ajv@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("  resolved \"http://nexus/nexus3/repository/npm-all/ajv/-/ajv-4.11.8.tgz#82ffb02b29e662ae53bdc20af15947706739c536\"");
        designedYarnLock.add("  dependencies:");
        designedYarnLock.add("    co \"^4.6.0\"");
        designedYarnLock.add("    tr46 \"~0.0.3\"");
        designedYarnLock.add("    cssstyle \">= 0.2.37 < 0.3.0\"");
        designedYarnLock.add("");
        designedYarnLock.add("co@^4.6.0:");
        designedYarnLock.add("  version \"4.6.0\"");
        designedYarnLock.add("  resolved \"http://nexus/nexus3/repository/npm-all/co/-/co-4.6.0.tgz#6ea6bdf3d853ae54ccb8e47bfa0bf3f9031fb184\"");
        designedYarnLock.add("  dependencies:");
        designedYarnLock.add("    hoek \"4.x.x\"");
        designedYarnLock.add("");
        designedYarnLock.add("tr46@~0.0.3:");
        designedYarnLock.add("  version \"0.0.3\"");
        designedYarnLock.add("  resolved \"http://nexus/nexus3/repository/npm-all/tr46/-/tr46-0.0.3.tgz#8184fd347dac9cdc185992f3a6622e14b9d9ab6a\"");
        designedYarnLock.add("");
        designedYarnLock.add("\"cssstyle@>= 0.2.37 < 0.3.0\":");
        designedYarnLock.add("  version \"0.2.37\"");
        designedYarnLock.add("  resolved \"http://nexus/nexus3/repository/npm-all/cssstyle/-/cssstyle-0.2.37.tgz#541097234cb2513c83ceed3acddc27ff27987d54\"");
        designedYarnLock.add("  dependencies:");
        designedYarnLock.add("    cssom \"0.3.x\"");
        designedYarnLock.add("hoek@4.x.x:");
        designedYarnLock.add("  version \"4.2.1\"");
        designedYarnLock.add("  resolved \"http://nexus/nexus3/repository/npm-all/hoek/-/hoek-4.2.1.tgz#9634502aa12c445dd5a7c5734b572bb8738aacbb\"");

        final List<String> yarnListText = FunctionalTestFiles.asListOfStrings("/yarn/yarn-list-res.txt");
        final DependencyGraph dependencyGraph = createDependencyGraph(designedYarnLock, yarnListText);
        GraphCompare.assertEqualsResource("/yarn/list-expected-graph-2.json", dependencyGraph);
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
