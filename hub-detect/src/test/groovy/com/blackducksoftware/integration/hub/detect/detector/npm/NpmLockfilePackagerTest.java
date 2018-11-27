package com.blackducksoftware.integration.hub.detect.detector.npm;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil;
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class NpmLockfilePackagerTest {
    NpmLockfilePackager npmLockfilePackager;
    TestUtil testUtil;

    @Before
    public void init() {
        testUtil = new TestUtil();
        npmLockfilePackager = new NpmLockfilePackager(new GsonBuilder().setPrettyPrinting().create(), new ExternalIdFactory());
    }

    @Test
    public void parseLockFileTest() {
        final String lockFileText = testUtil.getResourceAsUTF8String("/npm/package-lock.json");
        final NpmParseResult result = npmLockfilePackager.parse("source", lockFileText, true);

        Assert.assertEquals(result.projectName, "knockout-tournament");
        Assert.assertEquals(result.projectVersion, "1.0.0");
        DependencyGraphResourceTestUtil.assertGraph("/npm/packageLockExpected_graph.json", result.codeLocation.getDependencyGraph());
    }

    @Test
    public void parseShrinkwrapTest() {
        final String shrinkwrapText = testUtil.getResourceAsUTF8String("/npm/npm-shrinkwrap.json");
        final NpmParseResult result = npmLockfilePackager.parse("source", shrinkwrapText, true);

        Assert.assertEquals(result.projectName, "fec-builder");
        Assert.assertEquals(result.projectVersion, "1.3.7");
        DependencyGraphResourceTestUtil.assertGraph("/npm/shrinkwrapExpected_graph.json", result.codeLocation.getDependencyGraph());
    }
}
