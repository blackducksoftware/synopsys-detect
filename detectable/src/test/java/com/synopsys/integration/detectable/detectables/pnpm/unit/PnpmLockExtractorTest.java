package com.synopsys.integration.detectable.detectables.pnpm.unit;

import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.enums.DependencyType;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmLinkedPackageResolver;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmLockExtractor;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmLockYamlParser;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmYamlTransformer;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonFiles;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonReader;

public class PnpmLockExtractorTest {
    PackageJsonFiles packageJsonFiles = new PackageJsonFiles(new PackageJsonReader(new Gson()));
    PnpmLockExtractor extractor = new PnpmLockExtractor(new PnpmLockYamlParser(new PnpmYamlTransformer(new ExternalIdFactory())), packageJsonFiles);

    @Test
    public void testNoFailureOnNullPackageJson() {
        extractor.extract(new File("pnpm-lock.yaml"), null, Arrays.asList(DependencyType.OPTIONAL, DependencyType.DEV), new PnpmLinkedPackageResolver(new File(""), packageJsonFiles));
    }
}
