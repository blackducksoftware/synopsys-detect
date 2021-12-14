package com.synopsys.integration.detectable.detectables.pnpm.functional;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.enums.DependencyType;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmLinkedPackageResolver;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmLockYamlParser;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmYamlTransformer;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonFiles;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonReader;
import com.synopsys.integration.detectable.util.DependencyTypeFilter;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class PnpmLockYamlParserTest {

    @Test
    public void testParse() throws IOException, IntegrationException {
        File pnpmLockYaml = FunctionalTestFiles.asFile("/pnpm/pnpm-lock.yaml");
        DependencyTypeFilter dependencyTypeFilter = new DependencyTypeFilter(Arrays.asList(DependencyType.APP, DependencyType.DEV, DependencyType.OPTIONAL));
        PnpmLockYamlParser pnpmLockYamlParser = new PnpmLockYamlParser(new PnpmYamlTransformer(new ExternalIdFactory(), dependencyTypeFilter));
        PnpmLinkedPackageResolver pnpmLinkedPackageResolver = new PnpmLinkedPackageResolver(FunctionalTestFiles.asFile("/pnpm"), new PackageJsonFiles(new PackageJsonReader(new Gson())));

        List<CodeLocation> codeLocations = pnpmLockYamlParser.parse(pnpmLockYaml, new NameVersion("project", "version"), pnpmLinkedPackageResolver);
        Assertions.assertEquals(2, codeLocations.size());

        // Did we correctly identify root project package in "importers"?
        Assertions.assertTrue(codeLocations.stream()
            .map(CodeLocation::getExternalId)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .anyMatch(
                externalId -> externalId.getName().equals("project") && externalId.getVersion().equals("version")
            )
        );
    }

}
