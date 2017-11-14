package com.blackducksoftware.integration.hub.detect.bomtool.hex

import static com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphAssertions.*
import static org.junit.Assert.*

import org.junit.BeforeClass
import org.junit.Test
import org.springframework.test.util.ReflectionTestUtils

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
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

    public void testParseRebarTreeOutput(){
        DetectCodeLocation codeLocation = build('hex/dependencyTree.txt')
        DependencyGraph graph = codeLocation.dependencyGraph
    }

    private DetectCodeLocation build(String resource){
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
        ExternalId expectedExternalId  = externalIdFactory.createNameVersionExternalId(new Forge('hex', '/'), expectedName, expectedVersion)

        Dependency actualDependency = rebar3TreeParser.createDependencyFromLine('   │  │  └─ cf─0.2.2 (hex package)')

        assertEquals(expectedName, actualDependency.name)
        assertEquals(expectedVersion, actualDependency.version)
        assertEquals(expectedExternalId.name, actualDependency.externalId.name)
        assertEquals(expectedExternalId.version, actualDependency.externalId.version)
    }

    @Test
    public void testReduceLineToNameVersion() {
        assertEquals('qdate─0.4.2', rebar3TreeParser.reduceLineToNameVersion('   ├─ qdate─0.4.2 (git repo)'))
        assertEquals('erlware_commons─1.0.1', rebar3TreeParser.reduceLineToNameVersion('   │  ├─ erlware_commons─1.0.1 (hex package)'))
        assertEquals('cf─0.2.2', rebar3TreeParser.reduceLineToNameVersion('   │  │  └─ cf─0.2.2 (hex package)'))
    }

    @Test
    public void testGetDependencyLevelFromLine() {
        assertEquals(0, rebar3TreeParser.getDependencyLevelFromLine('└─ gcm─1.0.1 (project app)'))
        assertEquals(1, rebar3TreeParser.getDependencyLevelFromLine('   ├─ qdate─0.4.2 (git repo)'))
        assertEquals(2, rebar3TreeParser.getDependencyLevelFromLine('   │  ├─ erlware_commons─1.0.1 (hex package)'))
        assertEquals(3, rebar3TreeParser.getDependencyLevelFromLine('   │  │  └─ cf─0.2.2 (hex package)'))
        assertEquals(2, rebar3TreeParser.getDependencyLevelFromLine('   │  └─ qdate_localtime─1.1.0 (hex package)'))
        assertEquals(1, rebar3TreeParser.getDependencyLevelFromLine('   └─ webpush_encryption─0.0.1 (git repo)'))
        assertEquals(2, rebar3TreeParser.getDependencyLevelFromLine('      └─ base64url─0.0.1 (git repo)'))
    }

    @Test
    public void testIsProjectLine() {
        assertTrue(rebar3TreeParser.isProject('└─ gcm─1.0.1 (project app)'))
        assertFalse(rebar3TreeParser.isProject('   ├─ qdate─0.4.2 (git repo)'))
        assertFalse(rebar3TreeParser.isProject('   │  ├─ erlware_commons─1.0.1 (hex package)'))
    }
}
