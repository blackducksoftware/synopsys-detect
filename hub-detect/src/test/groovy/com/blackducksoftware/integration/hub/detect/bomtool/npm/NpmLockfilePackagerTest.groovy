package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.junit.Assert
import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.npm.parse.NpmLockfilePackager
import com.blackducksoftware.integration.hub.detect.bomtool.npm.parse.NpmParseResult
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import com.google.gson.GsonBuilder

class NpmLockfilePackagerTest {
    NpmLockfilePackager npmLockfilePackager
    TestUtil testUtil

    @Before
    public void init() {
        testUtil = new TestUtil()

        def externalIdFactory = new ExternalIdFactory()

        npmLockfilePackager = new NpmLockfilePackager()
        npmLockfilePackager.externalIdFactory = externalIdFactory
        npmLockfilePackager.gson = new GsonBuilder().setPrettyPrinting().create()
        npmLockfilePackager.nameVersionNodeTransformer = new NameVersionNodeTransformer(externalIdFactory)
    }

    @Test
    public void parseLockFileTest() {
        String lockFileText = testUtil.getResourceAsUTF8String('/npm/package-lock.json')
        NpmParseResult result = npmLockfilePackager.parse("source", lockFileText, true)

        Assert.assertEquals(result.projectName, "knockout-tournament");
        Assert.assertEquals(result.projectVersion, "1.0.0");
        DependencyGraphResourceTestUtil.assertGraph('/npm/packageLockExpected_graph.json', result.codeLocation.dependencyGraph);
    }

    @Test
    public void parseShrinkwrapTest() {
        String shrinkwrapText = testUtil.getResourceAsUTF8String('/npm/npm-shrinkwrap.json')
        NpmParseResult result = npmLockfilePackager.parse("source", shrinkwrapText, true)

        Assert.assertEquals(result.projectName, "fec-builder");
        Assert.assertEquals(result.projectVersion, "1.3.7");
        DependencyGraphResourceTestUtil.assertGraph('/npm/shrinkwrapExpected_graph.json', result.codeLocation.dependencyGraph);
    }
}
