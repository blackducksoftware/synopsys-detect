package com.blackducksoftware.integration.hub.detect.bomtool.hex;

import static com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil.assertGraph;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.hub.bdio.model.Forge;
import com.synopsys.integration.hub.bdio.model.dependency.Dependency;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class RebarParserTest {

    static Rebar3TreeParser rebar3TreeParser;
    static TestUtil testUtil;

    static ExternalIdFactory externalIdFactory;

    @BeforeClass
    public static void setup() {
        externalIdFactory = new ExternalIdFactory();
        rebar3TreeParser = new Rebar3TreeParser(externalIdFactory);
        testUtil = new TestUtil();
    }

    @Test
    public void testParseRebarTreeOutput() {
        final MutableMapDependencyGraph expectedGraph = new MutableMapDependencyGraph();
        final Dependency gitInnerParentDependency = buildDependency("git_inner_parent_dependency", "0.0.2");
        final Dependency hexInnerChildDependency = buildDependency("hex_inner_child_dependency", "0.3.0");
        final Dependency hexGrandchildDependency = buildDependency("hex_grandchild_dependency", "4.0.0");
        final Dependency gitInnerChildDependency = buildDependency("git_inner_child_dependency", "0.5.0");
        final Dependency gitGrandchildDependency = buildDependency("git_grandchild_dependency", "6.0.0");
        final Dependency gitOuterParentDependency = buildDependency("git_outer_parent_dependency", "0.0.7");
        final Dependency gitOuterChildDependency = buildDependency("git_outer_child_dependency", "0.8.0");

        expectedGraph.addChildrenToRoot(gitInnerParentDependency, gitOuterParentDependency);
        expectedGraph.addChildWithParent(hexInnerChildDependency, gitInnerParentDependency);
        expectedGraph.addChildWithParents(hexGrandchildDependency, hexInnerChildDependency);

        expectedGraph.addChildWithParent(gitInnerChildDependency, gitInnerParentDependency);
        expectedGraph.addChildWithParents(gitGrandchildDependency, gitInnerChildDependency);

        expectedGraph.addChildWithParents(gitOuterChildDependency, gitOuterParentDependency);

        final DetectCodeLocation codeLocation = build("/hex/dependencyTree.txt");
        final DependencyGraph actualGraph = codeLocation.getDependencyGraph();

        assertGraph(expectedGraph, actualGraph);
    }

    private Dependency buildDependency(final String name, final String version) {
        return new Dependency(name, version, externalIdFactory.createNameVersionExternalId(Forge.HEX, name, version));
    }

    private DetectCodeLocation build(final String resource) {
        final List<String> dependencyTreeOutput = Arrays.asList(testUtil.getResourceAsUTF8String(resource).split(System.lineSeparator()));
        final Rebar3TreeParser rebarTreeParser = new Rebar3TreeParser(externalIdFactory);
        final RebarParseResult result = rebarTreeParser.parseRebarTreeOutput(BomToolType.REBAR, dependencyTreeOutput, "");

        return result.getCodeLocation();
    }

    @Test
    public void testCreateDependencyFromLine() {
        final String expectedName = "cf";
        final String expectedVersion = "0.2.2";
        final ExternalId expectedExternalId = externalIdFactory.createNameVersionExternalId(Forge.HEX, expectedName, expectedVersion);

        final Dependency actualDependency = rebar3TreeParser.createDependencyFromLine("   \u2502  \u2502  \u2514\u2500 cf\u25000.2.2 (hex package)");

        assertEquals(expectedName, actualDependency.name);
        assertEquals(expectedVersion, actualDependency.version);
        assertEquals(expectedExternalId.name, actualDependency.externalId.name);
        assertEquals(expectedExternalId.version, actualDependency.externalId.version);
    }

    @Test
    public void testReduceLineToNameVersion() {
        assertEquals("qdate\u25000.4.2", rebar3TreeParser.reduceLineToNameVersion("   \u251C\u2500 qdate\u25000.4.2 (git repo)"));
        assertEquals("erlware_commons\u25001.0.1", rebar3TreeParser.reduceLineToNameVersion("   \u2502  \u251C\u2500 erlware_commons\u25001.0.1 (hex package)"));
        assertEquals("cf\u25000.2.2", rebar3TreeParser.reduceLineToNameVersion("   \u2502  \u2502  \u2514\u2500 cf\u25000.2.2 (hex package)"));
    }

    @Test
    public void testGetDependencyLevelFromLine() {
        assertEquals(0, rebar3TreeParser.getDependencyLevelFromLine("\u2514\u2500 gcm\u25001.0.1 (project app)"));
        assertEquals(1, rebar3TreeParser.getDependencyLevelFromLine("   \u251C\u2500 qdate\u25000.4.2 (git repo)"));
        assertEquals(2, rebar3TreeParser.getDependencyLevelFromLine("   \u2502  \u251C\u2500 erlware_commons\u25001.0.1 (hex package)"));
        assertEquals(3, rebar3TreeParser.getDependencyLevelFromLine("   \u2502  \u2502  \u2514\u2500 cf\u25000.2.2 (hex package)"));
        assertEquals(2, rebar3TreeParser.getDependencyLevelFromLine("   \u2502  \u2514\u2500 qdate_localtime\u25001.1.0 (hex package)"));
        assertEquals(1, rebar3TreeParser.getDependencyLevelFromLine("   \u2514\u2500 webpush_encryption\u25000.0.1 (git repo)"));
        assertEquals(2, rebar3TreeParser.getDependencyLevelFromLine("      \u2514\u2500 base64url\u25000.0.1 (git repo)"));
    }

    @Test
    public void testIsProjectLine() {
        assertTrue(rebar3TreeParser.isProject("\u2514\u2500 gcm\u25001.0.1 (project app)"));
        assertFalse(rebar3TreeParser.isProject("   \u251C\u2500 qdate\u25000.4.2 (git repo)"));
        assertFalse(rebar3TreeParser.isProject("   \u2502  \u251C\u2500 erlware_commons\u25001.0.1 (hex package)"));
    }
}
