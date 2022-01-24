package com.synopsys.integration.detectable.detectables.npm.packagejson.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.npm.NpmDependencyType;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectable;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectableOptions;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

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
