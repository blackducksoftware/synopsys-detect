package com.blackduck.integration.detectable.detectables.npm.packagejson.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.detectables.npm.NpmDependencyType;
import com.blackduck.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectable;
import com.blackduck.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectableOptions;
import com.blackduck.integration.detectable.util.MockDetectableEnvironment;
import com.blackduck.integration.detectable.util.MockFileFinder;

public class NpmPackageJsonParseDetectableTest {
    private static final String PACKAGE_JSON_FILENAME = "package.json";

    @Test
    public void testApplicable() {
        DetectableEnvironment environment = MockDetectableEnvironment.empty();
        FileFinder fileFinder = MockFileFinder.withFileNamed(PACKAGE_JSON_FILENAME);

        EnumListFilter<NpmDependencyType> npmDependencyTypeFilter = EnumListFilter.fromExcluded(NpmDependencyType.DEV, NpmDependencyType.PEER);
        NpmPackageJsonParseDetectableOptions npmPackageJsonParseDetectableOptions = new NpmPackageJsonParseDetectableOptions(npmDependencyTypeFilter);

        NpmPackageJsonParseDetectable detectable = new NpmPackageJsonParseDetectable(environment, fileFinder, null);

        assertTrue(detectable.applicable().getPassed());
    }

}
