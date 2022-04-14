package com.synopsys.integration.detectable.detectables.pnpm.functional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmDependencyType;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmLinkedPackageResolver;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmLockYamlParser;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmYamlTransformer;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonFiles;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonReader;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class PnpmLockYamlParserTest {

    @Test
    public void testParse() throws IOException, IntegrationException {
        File pnpmLockYaml = FunctionalTestFiles.asFile("/pnpm/pnpm-lock.yaml");
        EnumListFilter<PnpmDependencyType> dependencyTypeFilter = EnumListFilter.excludeNone();
        PnpmLockYamlParser pnpmLockYamlParser = new PnpmLockYamlParser(new PnpmYamlTransformer(dependencyTypeFilter));
        PnpmLinkedPackageResolver pnpmLinkedPackageResolver = new PnpmLinkedPackageResolver(
            FunctionalTestFiles.asFile("/pnpm"),
            new PackageJsonFiles(new PackageJsonReader(new Gson()))
        );

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

        // Do all code locations have a source path?
        Assertions.assertAll(codeLocations.stream()
            .map(codeLocation -> () -> Assertions.assertTrue(codeLocation.getSourcePath().isPresent(), String.format(
                "Expected source path to be present for all code locations. But code location with id %s does not have one set.",
                codeLocation.getExternalId().map(ExternalId::createExternalId).orElse("N/A")
            ))));

        // Did we generate a unique source path for each code location?
        Map<String, List<File>> collect = codeLocations.stream()
            .map(CodeLocation::getSourcePath)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.groupingBy(File::getAbsolutePath));

        Assertions.assertAll(collect.entrySet().stream()
            .map(codeLocationGrouping -> () -> {
                int numberOfCodeLocations = codeLocationGrouping.getValue().size();
                Assertions.assertEquals(
                    1,
                    numberOfCodeLocations,
                    String.format("Expected unique code locations paths. But found %d with that same path of %s", numberOfCodeLocations, codeLocationGrouping.getKey())
                );
            }));
    }
}
