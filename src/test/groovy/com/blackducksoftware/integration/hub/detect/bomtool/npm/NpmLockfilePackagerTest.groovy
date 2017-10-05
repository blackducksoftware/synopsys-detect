package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.junit.Assert
import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphTestUtil
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
        String lockFileText = testUtil.getResourceAsUTF8String('npm/package-lock.json')
        DetectCodeLocation actual = npmLockfilePackager.parse("source", lockFileText)

        Assert.assertEquals(actual.bomToolProjectName, "knockout-tournament");
        Assert.assertEquals(actual.bomToolProjectVersionName, "1.0.0");
        DependencyGraphTestUtil.assertGraph('/npm/packageLockExpected_graph.json', actual.dependencyGraph);
    }

    @Test
    public void parseShrinkwrapTest() {
        String shrinkwrapText = testUtil.getResourceAsUTF8String('npm/npm-shrinkwrap.json')
        DetectCodeLocation actual = npmLockfilePackager.parse("source", shrinkwrapText)

        Assert.assertEquals(actual.bomToolProjectName, "fec-builder");
        Assert.assertEquals(actual.bomToolProjectVersionName, "1.3.7");
        DependencyGraphTestUtil.assertGraph('/npm/shrinkwrapExpected_graph.json', actual.dependencyGraph);
    }
}
