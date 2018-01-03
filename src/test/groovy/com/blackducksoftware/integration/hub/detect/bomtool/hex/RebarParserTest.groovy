package com.blackducksoftware.integration.hub.detect.bomtool.hex

import static com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphAssertions.*
import static com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil.*
import static org.junit.Assert.*

import org.junit.BeforeClass
import org.junit.Test
import org.springframework.test.util.ReflectionTestUtils

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil

class RebarParserTest {

    static Rebar3TreeParser rebar3TreeParser
    static TestUtil testUtil
    static ExternalIdFactory externalIdFactory

    @BeforeClass
    public static void setup() {
        rebar3TreeParser = new Rebar3TreeParser()
        externalIdFactory = new ExternalIdFactory()
        rebar3TreeParser.externalIdFactory = externalIdFactory
        testUtil = new TestUtil()
    }

    @Test
    public void testParseRebarTreeOutput() {
        MutableMapDependencyGraph expectedGraph = new MutableMapDependencyGraph()
        Dependency gitInnerParentDependency = buildDependency('git_inner_parent_dependency', '0.0.2')
        Dependency hexInnerChildDependency = buildDependency('hex_inner_child_dependency', '0.3.0')
        Dependency hexGrandchildDependency = buildDependency('hex_grandchild_dependency', '4.0.0')
        Dependency gitInnerChildDependency = buildDependency('git_inner_child_dependency', '0.5.0')
        Dependency gitGrandchildDependency = buildDependency('git_grandchild_dependency', '6.0.0')
        Dependency gitOuterParentDependency = buildDependency('git_outer_parent_dependency', '0.0.7')
        Dependency gitOuterChildDependency = buildDependency('git_outer_child_dependency', '0.8.0')

        expectedGraph.addChildrenToRoot(gitInnerParentDependency, gitOuterParentDependency)
        expectedGraph.addChildWithParent(hexInnerChildDependency, gitInnerParentDependency)
        expectedGraph.addChildWithParents(hexGrandchildDependency, hexInnerChildDependency)

        expectedGraph.addChildWithParent(gitInnerChildDependency, gitInnerParentDependency)
        expectedGraph.addChildWithParents(gitGrandchildDependency, gitInnerChildDependency)

        expectedGraph.addChildWithParents(gitOuterChildDependency, gitOuterParentDependency)

        DetectCodeLocation codeLocation = build('hex/dependencyTree.txt')
        DependencyGraph actualGraph = codeLocation.dependencyGraph

        assertGraph(expectedGraph, actualGraph)
    }

    private Dependency buildDependency(String name, String version) {
        return new Dependency(name, version, externalIdFactory.createNameVersionExternalId(Forge.HEX, name, version))
    }

    private DetectCodeLocation build(String resource) {
        List<String> dependencyTreeOutput = testUtil.getResourceAsUTF8String(resource).split('\n')
        DetectProject project = new DetectProject()
        Rebar3TreeParser rebarTreeParser = new Rebar3TreeParser()
        ReflectionTestUtils.setField(rebarTreeParser, 'externalIdFactory', externalIdFactory)
        DetectCodeLocation codeLocation = rebarTreeParser.parseRebarTreeOutput(dependencyTreeOutput, project, '')

        return codeLocation
    }

    @Test
    public void testCreateDependencyFromLine() {
        String expectedName = 'cf'
        String expectedVersion = '0.2.2'
        ExternalId expectedExternalId  = externalIdFactory.createNameVersionExternalId(Forge.HEX, expectedName, expectedVersion)

        Dependency actualDependency = rebar3TreeParser.createDependencyFromLine('   \u2502  \u2502  \u2514\u2500 cf\u25000.2.2 (hex package)')

        assertEquals(expectedName, actualDependency.name)
        assertEquals(expectedVersion, actualDependency.version)
        assertEquals(expectedExternalId.name, actualDependency.externalId.name)
        assertEquals(expectedExternalId.version, actualDependency.externalId.version)
    }

    @Test
    public void testReduceLineToNameVersion() {
        assertEquals('qdate\u25000.4.2', rebar3TreeParser.reduceLineToNameVersion('   \u251C\u2500 qdate\u25000.4.2 (git repo)'))
        assertEquals('erlware_commons\u25001.0.1', rebar3TreeParser.reduceLineToNameVersion('   \u2502  \u251C\u2500 erlware_commons\u25001.0.1 (hex package)'))
        assertEquals('cf\u25000.2.2', rebar3TreeParser.reduceLineToNameVersion('   \u2502  \u2502  \u2514\u2500 cf\u25000.2.2 (hex package)'))
    }

    @Test
    public void testGetDependencyLevelFromLine() {
        assertEquals(0, rebar3TreeParser.getDependencyLevelFromLine('\u2514\u2500 gcm\u25001.0.1 (project app)'))
        assertEquals(1, rebar3TreeParser.getDependencyLevelFromLine('   \u251C\u2500 qdate\u25000.4.2 (git repo)'))
        assertEquals(2, rebar3TreeParser.getDependencyLevelFromLine('   \u2502  \u251C\u2500 erlware_commons\u25001.0.1 (hex package)'))
        assertEquals(3, rebar3TreeParser.getDependencyLevelFromLine('   \u2502  \u2502  \u2514\u2500 cf\u25000.2.2 (hex package)'))
        assertEquals(2, rebar3TreeParser.getDependencyLevelFromLine('   \u2502  \u2514\u2500 qdate_localtime\u25001.1.0 (hex package)'))
        assertEquals(1, rebar3TreeParser.getDependencyLevelFromLine('   \u2514\u2500 webpush_encryption\u25000.0.1 (git repo)'))
        assertEquals(2, rebar3TreeParser.getDependencyLevelFromLine('      \u2514\u2500 base64url\u25000.0.1 (git repo)'))
    }

    @Test
    public void testIsProjectLine() {
        assertTrue(rebar3TreeParser.isProject('\u2514\u2500 gcm\u25001.0.1 (project app)'))
        assertFalse(rebar3TreeParser.isProject('   \u251C\u2500 qdate\u25000.4.2 (git repo)'))
        assertFalse(rebar3TreeParser.isProject('   \u2502  \u251C\u2500 erlware_commons\u25001.0.1 (hex package)'))
    }
}
