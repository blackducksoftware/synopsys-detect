package com.blackducksoftware.integration.hub.detect.bomtool.yarn;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil;
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class YarnBomToolTest {
    private YarnBomTool yarnBomTool;
    private DependencyGraph dependencyGraph;
    private List<String> testLines;
    private final TestUtil testUtil = new TestUtil();

    @Test
    public void parseYarnListTest() {
        List<String> designedYarnLock = new ArrayList<>();
        designedYarnLock.add("async@~0.9.0:");
        designedYarnLock.add("  version \"0.9.2\"");
        designedYarnLock.add("  resolved \"http://nexus/nexus3/repository/npm-all/async/-/async-0.9.2.tgz#aea74d5e61c1f899613bf64bda66d4c78f2fd17d\"");
        designedYarnLock.add("  dependencies:");
        designedYarnLock.add("    minimist \"0.0.8\"");
        designedYarnLock.add("");
        designedYarnLock.add("minimist@0.0.8:");
        designedYarnLock.add("  version \"0.0.8\"");
        designedYarnLock.add("  resolved \"http://nexus/nexus3/repository/npm-all/minimist/-/minimist-0.0.8.tgz#857fcabfc3397d2625b8228262e86aa7a011b05d\"");


        yarnBomTool = new YarnBomTool(designedYarnLock);
        String yarnListText = testUtil.getResourceAsUTF8String("/yarn/yarn.list.txt");
        ExecutableOutput exeOutput = new ExecutableOutput(yarnListText, "");
        DependencyGraph dependencyGraph = yarnBomTool.parseYarnList(exeOutput.getStandardOutputAsList());
        DependencyGraphResourceTestUtil.assertGraph("/yarn/list_expected_graph.json", dependencyGraph);
    }

    @Test
    public void parseYarnListWithResolvableVersions() {
        List<String> designedYarnLock = new ArrayList<>();
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


        yarnBomTool = new YarnBomTool(designedYarnLock);
        String yarnListText = testUtil.getResourceAsUTF8String("/yarn/yarn.list.res.txt");
        ExecutableOutput exeOutput = new ExecutableOutput(yarnListText, "");
        DependencyGraph dependencyGraph = yarnBomTool.parseYarnList(exeOutput.getStandardOutputAsList());
        DependencyGraphResourceTestUtil.assertGraph("/yarn/list_expected_graph_2.json", dependencyGraph);
    }

    @Test
    public void testThatYarnListLineAtBeginningIsIgnored() {
        testLines = new ArrayList<>();
        testLines.add("yarn list v1.5.1");
        testLines.add("├─ abab@1.0.4");

        yarnBomTool = new YarnBomTool(testLines);
        dependencyGraph = yarnBomTool.parseYarnList(testLines);

        List<ExternalId> tempList = new ArrayList<>(dependencyGraph.getRootDependencyExternalIds());

        assertNotNull(tempList.get(0));
        assertEquals(1, tempList.size());
    }

    @Test
    public void testThatYarnListWithOnlyTopLevelDependenciesIsParsedCorrectly() {
        testLines = new ArrayList<>();
        testLines.add("├─ esprima@3.1.3");
        testLines.add("└─ extsprintf@1.3.0");

        yarnBomTool = new YarnBomTool(testLines);
        dependencyGraph = yarnBomTool.parseYarnList(testLines);

        List<ExternalId> tempList = new ArrayList<>(dependencyGraph.getRootDependencyExternalIds());

        assertListContainsDependency("esprima", tempList);
        assertListContainsDependency("extsprintf", tempList);
    }


    @Test
    public void testThatYarnListWithGrandchildIsParsedCorrectly() {
        testLines = new ArrayList<>();
        testLines.add("├─ yargs-parser@4.2.1");
        testLines.add("│  └─ camelcase@^3.0.0");

        yarnBomTool = new YarnBomTool(testLines);
        dependencyGraph = yarnBomTool.parseYarnList(testLines);

        List<ExternalId> tempList = new ArrayList<>(dependencyGraph.getRootDependencyExternalIds());
        List<ExternalId> kidsList = new ArrayList<>();
        for (int i = 0; i < tempList.size(); i++) {
            if ("yargs-parser".equals(tempList.get(i).name))
                kidsList = new ArrayList<>(dependencyGraph.getChildrenExternalIdsForParent(tempList.get(i)));
        }

        assertListContainsDependency("yargs-parser", tempList);
        assertListContainsDependency("camelcase", kidsList);
    }

    @Test
    public void testThatYarnListWithGreatGrandchildrenIsParsedCorrectly() {
        testLines = new ArrayList<>();
        testLines.add("├─ yargs-parser@4.2.1");
        testLines.add("│  └─ camelcase@^3.0.0");
        testLines.add("│  │  └─ ms@0.7.2");

        yarnBomTool = new YarnBomTool(testLines);
        dependencyGraph = yarnBomTool.parseYarnList(testLines);

        List<ExternalId> tempList = new ArrayList<>(dependencyGraph.getRootDependencyExternalIds());
        List<ExternalId> kidsList = new ArrayList<>();
        for (int i = 0; i < tempList.size(); i++) {
            if ("yargs-parser".equals(tempList.get(i).name))
                kidsList = new ArrayList<>(dependencyGraph.getChildrenExternalIdsForParent(tempList.get(i)));
        }
        System.out.println(tempList);
        System.out.println(kidsList);

        assertListContainsDependency("yargs-parser", tempList);
        assertListContainsDependency("camelcase", kidsList);
        assertListContainsDependency("ms", kidsList);
    }

    private void assertListContainsDependency(String dep, List<ExternalId> list) {
        System.out.println(dep);
        for (int i = 0; i < list.size(); i++) {
            if (dep.equals(list.get(i).name)) {
                assertTrue(true);
                return;
            }
        }
        fail();
    }

    @Test
    public void testThatYarnListRegexParsesTheCorrectText() {
        String input = "│  │  ├─ engine.io-client@~1.8.4";
        assertEquals("engine.io-client@~1.8.4", YarnBomTool.grabFuzzyName(input));

        input = "│  ├─ test-fixture@PolymerElements/test-fixture";
        assertEquals("test-fixture@PolymerElements/test-fixture", YarnBomTool.grabFuzzyName(input));

        input = "│  │  ├─ tough-cookie@>=0.12.0";
        assertEquals("tough-cookie@>=0.12.0", YarnBomTool.grabFuzzyName(input));

        input = "│  │  ├─ cryptiles@2.x.x";
        assertEquals("cryptiles@2.x.x", YarnBomTool.grabFuzzyName(input));

        input = "│  │  ├─ asn1@0.2.3";
        assertEquals("asn1@0.2.3", YarnBomTool.grabFuzzyName(input));

        input = "│  ├─ cssom@>= 0.3.2 < 0.4.0";
        assertEquals("cssom@>= 0.3.2 < 0.4.0", YarnBomTool.grabFuzzyName(input));

    }
}
