package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.junit.Assert
import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import com.google.gson.GsonBuilder

public class NpmLockfilePackagerTest {
    NpmLockfilePackager npmLockfilePackager
    TestUtil testUtil

    @Before
    public void init() {
        testUtil = new TestUtil()
        npmLockfilePackager = new NpmLockfilePackager(new GsonBuilder().setPrettyPrinting().create(), new ExternalIdFactory())
    }

    @Test
    public void parseLockFileTest() {
        String lockFileText = testUtil.getResourceAsUTF8String('/npm/package-lock.json')
        NpmParseResult result = npmLockfilePackager.parse(BomToolType.NPM_PACKAGELOCK, "source", lockFileText, true)

        Assert.assertEquals(result.projectName, "knockout-tournament");
        Assert.assertEquals(result.projectVersion, "1.0.0");
        DependencyGraphResourceTestUtil.assertGraph('/npm/packageLockExpected_graph.json', result.codeLocation.dependencyGraph);
    }

    @Test
    public void parseShrinkwrapTest() {
        String shrinkwrapText = testUtil.getResourceAsUTF8String('/npm/npm-shrinkwrap.json')
        NpmParseResult result = npmLockfilePackager.parse(BomToolType.NPM_SHRINKWRAP, "source", shrinkwrapText, true)

        Assert.assertEquals(result.projectName, "fec-builder");
        Assert.assertEquals(result.projectVersion, "1.3.7");
        DependencyGraphResourceTestUtil.assertGraph('/npm/shrinkwrapExpected_graph.json', result.codeLocation.dependencyGraph);
    }
}
