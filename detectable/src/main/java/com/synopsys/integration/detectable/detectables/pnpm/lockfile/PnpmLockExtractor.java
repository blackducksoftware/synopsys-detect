package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmLinkedPackageResolver;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmLockYamlParser;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.NullSafePackageJson;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonFiles;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.util.NameVersion;

public class PnpmLockExtractor {
    private final PnpmLockYamlParser pnpmLockYamlParser;
    private final PackageJsonFiles packageJsonFiles;

    public PnpmLockExtractor(PnpmLockYamlParser pnpmLockYamlParser, PackageJsonFiles packageJsonFiles) {
        this.pnpmLockYamlParser = pnpmLockYamlParser;
        this.packageJsonFiles = packageJsonFiles;
    }

    public Extraction extract(File yarnLockYamlFile, @Nullable File packageJsonFile, PnpmLinkedPackageResolver linkedPackageResolver) {
        try {
            Optional<NameVersion> nameVersion = parseNameVersionFromPackageJson(packageJsonFile);
            List<CodeLocation> codeLocations = pnpmLockYamlParser.parse(yarnLockYamlFile, nameVersion.orElse(null), linkedPackageResolver);
            return new Extraction.Builder().success(codeLocations)
                .nameVersionIfPresent(nameVersion)
                .build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private Optional<NameVersion> parseNameVersionFromPackageJson(@Nullable File packageJsonFile) throws IOException {
        if (packageJsonFile != null) {
            NullSafePackageJson nullSafePackageJson = packageJsonFiles.read(packageJsonFile);
            if (nullSafePackageJson.getName().isPresent() && nullSafePackageJson.getVersion().isPresent()) {
                return Optional.of(new NameVersion(nullSafePackageJson.getNameString(), nullSafePackageJson.getVersionString()));
            }
        }
        return Optional.empty();
    }
}
