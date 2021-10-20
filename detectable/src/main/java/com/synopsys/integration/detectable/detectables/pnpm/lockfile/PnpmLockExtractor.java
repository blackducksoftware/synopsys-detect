package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.util.NameVersion;

public class PnpmLockExtractor {
    private final Gson gson;
    private final PnpmLockYamlParser pnpmLockYamlParser;

    public PnpmLockExtractor(Gson gson, PnpmLockYamlParser pnpmLockYamlParser) {
        this.gson = gson;
        this.pnpmLockYamlParser = pnpmLockYamlParser;
    }

    public Extraction extract(File yarnLockYamlFile, @Nullable File packageJsonFile) {
        try {
            CodeLocation codeLocation = pnpmLockYamlParser.parse(yarnLockYamlFile, true);
            Optional<NameVersion> nameVersion = parseNameVersionFromPackageJson(packageJsonFile);
            return new Extraction.Builder().success(codeLocation)
                       .nameVersionIfPresent(nameVersion)
                       .build();
        } catch (IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private Optional<NameVersion> parseNameVersionFromPackageJson(File packageJsonFile) throws IOException {
        String packageJsonText = FileUtils.readFileToString(packageJsonFile, StandardCharsets.UTF_8);
        PackageJson packageJson = gson.fromJson(packageJsonText, PackageJson.class);
        String projectName = packageJson.name;
        String projectVersion = packageJson.version;
        if (projectName != null && projectVersion != null) {
            return Optional.of(new NameVersion(projectName, projectVersion));
        }
        return Optional.empty();
    }
}
