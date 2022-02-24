package com.synopsys.integration.detectable.detectables.lerna.unit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.lerna.LernaDetectable;
import com.synopsys.integration.detectable.detectables.lerna.LernaOptions;
import com.synopsys.integration.detectable.detectables.lerna.LernaPackageType;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.util.NameVersion;

public class LernaExternalDetectableTest extends DetectableFunctionalTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public LernaExternalDetectableTest() throws IOException {
        super("lerna");
    }

    @Override
    public void setup() throws IOException {
        // Package is not in package lock file but is present in lerna.
        addFile("lerna.json");

        addPackageJson(Paths.get(""), "lernaTest", "package-version", new NameVersion("concat-map", "~1"));

        addFile(
            Paths.get("package-lock.json"),
            "{",
            "   \"name\": \"lerna-project-name\",",
            "   \"version\": \"1.0.0\",",
            "   \"dependencies\": {",
            "   }",
            "}"
        );

        ExecutableOutput executableOutput = createStandardOutput(
            "[",
            "  {",
            "    \"name\": \"packageA\",",
            "    \"version\": \"1.2.3\",",
            "    \"private\": false,",
            "    \"location\": " + gson.toJson(getSourceDirectory() + "/packages/packageA"),
            "  },",
            "  {",
            "    \"name\": \"packageB\",",
            "    \"version\": \"3.2.1\",",
            "    \"private\": false,",
            "    \"location\": " + gson.toJson(getSourceDirectory() + "/packages/packageB"),
            "  }",
            "]"
        );
        addExecutableOutput(executableOutput, "lerna", "ls", "--all", "--json");

        Path packagesDirectory = addDirectory(Paths.get("packages"));

        Path packageADirectory = addDirectory(packagesDirectory.resolve("packageA"));
        addPackageJson(packageADirectory, "packageA", "1.2.3",
            new NameVersion("packageB", "~1")
        );

        Path packageBDirectory = addDirectory(packagesDirectory.resolve("packageB"));
        addPackageJson(packageBDirectory, "packageB", "3.2.1");

    }

    private void addPackageJson(Path directory, String packageName, String packageVersion, NameVersion... dependencies) throws IOException {
        PackageJson packageJson = new PackageJson();
        packageJson.name = packageName;
        packageJson.version = packageVersion;
        packageJson.dependencies = Arrays.stream(dependencies)
            .collect(Collectors.toMap(NameVersion::getName, NameVersion::getVersion));

        addFile(directory.resolve(LernaDetectable.PACKAGE_JSON), gson.toJson(packageJson));
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment environment) {
        NpmLockfileOptions npmLockFileOptions = new NpmLockfileOptions(EnumListFilter.excludeNone());
        YarnLockOptions yarnLockOptions = new YarnLockOptions(EnumListFilter.excludeNone(), new ArrayList<>(0), new ArrayList<>(0));
        LernaOptions lernaOptions = new LernaOptions(EnumListFilter.fromExcluded(LernaPackageType.PRIVATE), new LinkedList<>(), new LinkedList<>());
        return detectableFactory.createLernaDetectable(environment, () -> ExecutableTarget.forCommand("lerna"), npmLockFileOptions, lernaOptions, yarnLockOptions);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(3, extraction.getCodeLocations().size(), "Expected one code location from root, and two from a non-private packages.");

        CodeLocation codeLocationA = extraction.getCodeLocations().get(1);
        Assertions.assertEquals("packageA", codeLocationA.getExternalId().map(ExternalId::getName).orElse(null), "Package A should be the second code location.");
        NameVersionGraphAssert rootGraphAssert = new NameVersionGraphAssert(Forge.NPMJS, codeLocationA.getDependencyGraph());
        rootGraphAssert.hasDependency("packageB", "3.2.1");
    }

}
