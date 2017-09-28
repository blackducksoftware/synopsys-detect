package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import com.google.gson.GsonBuilder

class NpmLockfilePackagerTest {
    NpmLockfilePackager npmLockfilePackager
    TestUtil testUtil

    @Before
    public void init() {
        testUtil = new TestUtil()

        NameVersionNodeTransformer nameVersionNodeTransformer = new NameVersionNodeTransformer()
        nameVersionNodeTransformer.externalIdFactory = new ExternalIdFactory()

        npmLockfilePackager = new NpmLockfilePackager()
        npmLockfilePackager.gson = new GsonBuilder().setPrettyPrinting().create()
        npmLockfilePackager.nameVersionNodeTransformer = nameVersionNodeTransformer
    }

    @Test
    public void parseLockFileTest() {
        String lockFileText = testUtil.getResourceAsUTF8String('npm/package-lock.json')
        DependencyNode actual = npmLockfilePackager.parse(lockFileText)
        testUtil.testJsonResource('npm/packageLockExpected.json', actual)
    }

    @Test
    public void parseShrinkwrapTest() {
        String shrinkwrapText = testUtil.getResourceAsUTF8String('npm/package-lock.json')
        DependencyNode actual = npmLockfilePackager.parse(shrinkwrapText)
        testUtil.testJsonResource('npm/shrinkwrapExpected.json', actual)
    }
}
