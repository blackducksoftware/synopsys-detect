package com.blackducksoftware.integration.hub.detect.detector.npm;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.detector.npm.model.PackageJson;
import com.blackducksoftware.integration.hub.detect.detector.npm.model.PackageLock;
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil;
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class NpmLockfileParserTest {
    NpmLockfileParser npmLockfileParser;
    TestUtil testUtil;

    @Before
    public void init() {
        testUtil = new TestUtil();
        npmLockfileParser = new NpmLockfileParser(new GsonBuilder().setPrettyPrinting().create(), new ExternalIdFactory());
    }

    @Test
    public void parseLockFileTest() {
        final String lockFileText = testUtil.getResourceAsUTF8String("/npm/package-lock.json");

        final NpmParseResult result = npmLockfileParser.parse("source", recreatePackageJsonFromLock(lockFileText), lockFileText, true);

        Assert.assertEquals(result.projectName, "knockout-tournament");
        Assert.assertEquals(result.projectVersion, "1.0.0");
        DependencyGraphResourceTestUtil.assertGraph("/npm/packageLockExpected_graph.json", result.codeLocation.getDependencyGraph());
    }

    private Optional<String> recreatePackageJsonFromLock(String lockFileText) {
        //These tests were written before we needed a package json.
        //So we replicate a package json with every package as root.
        PackageJson packageJson = new PackageJson();
        Gson gson = new Gson();
        PackageLock packageLock = gson.fromJson(lockFileText, PackageLock.class);
        packageLock.dependencies.forEach((key, value) -> packageJson.dependencies.put(key, key));
        String text = gson.toJson(packageJson);
        return Optional.of(text);
    }

    @Test
    public void parseShrinkwrapTest() {
        final String shrinkwrapText = testUtil.getResourceAsUTF8String("/npm/npm-shrinkwrap.json");
        final NpmParseResult result = npmLockfileParser.parse("source", recreatePackageJsonFromLock(shrinkwrapText), shrinkwrapText, true);

        Assert.assertEquals(result.projectName, "fec-builder");
        Assert.assertEquals(result.projectVersion, "1.3.7");
        DependencyGraphResourceTestUtil.assertGraph("/npm/shrinkwrapExpected_graph.json", result.codeLocation.getDependencyGraph());
    }
}
