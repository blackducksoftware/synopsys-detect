package com.blackducksoftware.integration.hub.detect.bomtool.yarn

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import org.junit.Test

import static org.junit.Assert.*

public class YarnListParserTest {
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

        YarnListParser yarnListParser = new YarnListParser();
        String yarnListText = testUtil.getResourceAsUTF8String("/yarn/yarn.list.txt");
        DependencyGraph dependencyGraph = yarnListParser.parseYarnList(designedYarnLock, Arrays.asList(yarnListText.split(System.lineSeparator())));
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

        YarnListParser yarnListParser = new YarnListParser();
        String yarnListText = testUtil.getResourceAsUTF8String("/yarn/yarn.list.res.txt");
        DependencyGraph dependencyGraph = yarnListParser.parseYarnList(designedYarnLock, Arrays.asList(yarnListText.split(System.lineSeparator())));
        DependencyGraphResourceTestUtil.assertGraph("/yarn/list_expected_graph_2.json", dependencyGraph);
    }

    @Test
    public void testDependencyInYarnListAndNotInLock() {
        List<String> designedYarnLock = new ArrayList<>();
        designedYarnLock.add("ajv@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");

        List<String> testLines = new ArrayList<>();
        testLines.add("yarn list v1.5.1");
        testLines.add("├─ abab@1.0.4");

        YarnListParser yarnListParser = new YarnListParser();
        DependencyGraph dependencyGraph = yarnListParser.parseYarnList(designedYarnLock, testLines);

        List<ExternalId> tempList = new ArrayList<>(dependencyGraph.getRootDependencyExternalIds());

        assertEquals(0, tempList.size());
    }

    @Test
    public void testThatYarnListLineAtBeginningIsIgnored() {
        List<String> designedYarnLock = new ArrayList<>();
        designedYarnLock.add("abab@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");

        List<String> testLines = new ArrayList<>();
        testLines.add("yarn list v1.5.1");
        testLines.add("├─ abab@1.0.4");

        YarnListParser yarnListParser = new YarnListParser();
        DependencyGraph dependencyGraph = yarnListParser.parseYarnList(designedYarnLock, testLines);

        List<ExternalId> tempList = new ArrayList<>(dependencyGraph.getRootDependencyExternalIds());

        assertNotNull(tempList.get(0));
        assertEquals(1, tempList.size());
    }

    @Test
    public void testThatYarnListWithOnlyTopLevelDependenciesIsParsedCorrectly() {
        List<String> designedYarnLock = new ArrayList<>();
        designedYarnLock.add("esprima@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");
        designedYarnLock.add("extsprintf@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");

        List<String> testLines = new ArrayList<>();
        testLines.add("├─ esprima@3.1.3");
        testLines.add("└─ extsprintf@1.3.0");

        YarnListParser yarnListParser = new YarnListParser();
        DependencyGraph dependencyGraph = yarnListParser.parseYarnList(designedYarnLock, testLines);

        List<ExternalId> tempList = new ArrayList<>(dependencyGraph.getRootDependencyExternalIds());

        assertListContainsDependency("esprima", tempList);
        assertListContainsDependency("extsprintf", tempList);
    }

    @Test
    public void testThatYarnListWithGrandchildIsParsedCorrectly() {
        List<String> designedYarnLock = new ArrayList<>();
        designedYarnLock.add("yargs-parser@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");
        designedYarnLock.add("camelcase@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");

        List<String> testLines = new ArrayList<>();
        testLines.add("├─ yargs-parser@4.2.1");
        testLines.add("│  └─ camelcase@^3.0.0");

        YarnListParser yarnListParser = new YarnListParser();
        DependencyGraph dependencyGraph = yarnListParser.parseYarnList(designedYarnLock, testLines);

        List<ExternalId> tempList = new ArrayList<>(dependencyGraph.getRootDependencyExternalIds());
        List<ExternalId> kidsList = new ArrayList<>();
        for (int i = 0; i < tempList.size(); i++) {
            if ("yargs-parser".equals(tempList.get(i).name)) kidsList = new ArrayList<>(dependencyGraph.getChildrenExternalIdsForParent(tempList.get(i)));
        }

        assertListContainsDependency("yargs-parser", tempList);
        assertListContainsDependency("camelcase", kidsList);
    }

    @Test
    public void testThatYarnListWithGreatGrandchildrenIsParsedCorrectly() {
        List<String> designedYarnLock = new ArrayList<>();
        designedYarnLock.add("yargs-parser@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");
        designedYarnLock.add("camelcase@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");
        designedYarnLock.add("ms@5.5.2:");
        designedYarnLock.add("  version \"5.5.2\"");
        designedYarnLock.add("");

        List<String> testLines = new ArrayList<>();
        testLines.add("├─ yargs-parser@4.2.1");
        testLines.add("│  └─ camelcase@^3.0.0");
        testLines.add("│  │  └─ ms@0.7.2");

        YarnListParser yarnListParser = new YarnListParser();
        DependencyGraph dependencyGraph = yarnListParser.parseYarnList(designedYarnLock, testLines);

        List<ExternalId> tempList = new ArrayList<>(dependencyGraph.getRootDependencyExternalIds());
        List<ExternalId> kidsList = new ArrayList<>();
        for (int i = 0; i < tempList.size(); i++) {
            if ("yargs-parser".equals(tempList.get(i).name)) kidsList = new ArrayList<>(dependencyGraph.getChildrenExternalIdsForParent(tempList.get(i)));
        }
        System.out.println(tempList);
        System.out.println(kidsList);

        assertListContainsDependency("yargs-parser", tempList);
        assertListContainsDependency("camelcase", kidsList);
        assertListContainsDependency("ms", kidsList);
    }

    @Test
    public void testParseNameFromFuzzy() {
        YarnListParser yarnListParser = new YarnListParser();

        Optional<String> name = yarnListParser.parseNameFromFuzzy(null);
        assertFalse(name.isPresent());

        name = yarnListParser.parseNameFromFuzzy("");
        assertFalse(name.isPresent());

        name = yarnListParser.parseNameFromFuzzy("yargs-parser@5.5.2:");
        assertTrue(name.isPresent());
        assertEquals("yargs-parser", name.get());

        name = yarnListParser.parseNameFromFuzzy("@angular-devkit/core@0.0.20");
        assertTrue(name.isPresent());
        assertEquals("@angular-devkit/core", name.get());

        name = yarnListParser.parseNameFromFuzzy("@angular/common@5.2.5");
        assertTrue(name.isPresent());
        assertEquals("@angular/common", name.get());

        name = yarnListParser.parseNameFromFuzzy("@angular/common@5.2.5 >> @angular/common@5.5.2");
        assertTrue(name.isPresent());
        assertEquals("@angular/common", name.get());

        name = yarnListParser.parseNameFromFuzzy("tr46@~0.0.3 >> tr46@~0.0.3");
        assertTrue(name.isPresent());
        assertEquals("tr46", name.get());
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

}
