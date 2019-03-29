package com.synopsys.integration.detectable.detectables.npm.lockfile.functional;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLock;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfileParser;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;
import com.synopsys.integration.detectable.util.GraphSummary;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

@FunctionalTest
public class NpmDevExclusionTest {
    ExternalId childDev;
    ExternalId parentDev;
    NpmLockfileParser npmLockfileParser;
    String packageJsonText;
    String packageLockText;

    @BeforeEach
    void setup() {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();

        npmLockfileParser = new NpmLockfileParser(new GsonBuilder().setPrettyPrinting().create(), externalIdFactory);

        packageJsonText = FunctionalTestFiles.asString("/npm/dev-exclusion-test/package.json");
        packageLockText = FunctionalTestFiles.asString("/npm/dev-exclusion-test/package-lock.json");

        childDev = externalIdFactory.createNameVersionExternalId(Forge.NPM, "child-dev", "3.0.0");
        parentDev = externalIdFactory.createNameVersionExternalId(Forge.NPM, "parent-dev", "2.0.0");
    }

    @Test
    public void testDevDependencyNotExists() {
        final NpmParseResult result = npmLockfileParser.parse("source", Optional.of(packageJsonText), packageLockText, false);
        GraphAssert graphAssert = new GraphAssert(Forge.NPM, result.codeLocation.getDependencyGraph());
        graphAssert.hasNoDependency(childDev);
        graphAssert.hasNoDependency(parentDev);
        graphAssert.hasRootSize(0);
    }

    @Test
    public void testDevDependencyExists() {
        final NpmParseResult result = npmLockfileParser.parse("source", Optional.of(packageJsonText), packageLockText, true);
        GraphAssert graphAssert = new GraphAssert(Forge.NPM, result.codeLocation.getDependencyGraph());
        graphAssert.hasDependency(childDev);
        graphAssert.hasDependency(parentDev);
        graphAssert.hasRootSize(1);
    }
}
