package com.synopsys.integration.detectable.detectables.lerna.unit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectables.lerna.LernaDetectable;
import com.synopsys.integration.detectable.detectables.lerna.LernaOptions;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.util.NameVersion;

public class LernaDetectableTest extends DetectableFunctionalTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public LernaDetectableTest() throws IOException {
        super("lerna");
    }

    @Override
    public void setup() throws IOException {
        addFile("lerna.json");

        addPackageJson(Paths.get(""), "lernaTest", "package-version", new NameVersion("concat-map", "~1"));

        addFile(Paths.get("package-lock.json"),
            "{",
            "   \"name\": \"lerna-project-name\",",
            "   \"version\": \"1.0.0\",",
            "   \"dependencies\": {",
            "       \"balanced-match\": {",
            "           \"version\": \"1.0.0\",",
            "           \"resolved\": \"https://registry.npmjs.org/balanced-match/-/balanced-match-1.0.0.tgz\",",
            "           \"integrity\": \"sha1-ibTRmasr7kneFk6gK4nORi1xt2c=\",",
            "           \"dev\": true",
            "       },",
            "       \"brace-expansion\": {",
            "           \"version\": \"1.1.8\",",
            "           \"resolved\": \"https://registry.npmjs.org/brace-expansion/-/brace-expansion-1.1.8.tgz\",",
            "           \"integrity\": \"sha1-wHshHHyVLsH479Uad+8NHTmQopI=\",",
            "           \"dev\": true,",
            "           \"requires\": {",
            "               \"balanced-match\": \"1.0.0\",",
            "               \"concat-map\": \"0.0.1\"",
            "           }",
            "       },",
            "       \"concat-map\": {",
            "           \"version\": \"0.0.1\",",
            "           \"resolved\": \"https://registry.npmjs.org/concat-map/-/concat-map-0.0.1.tgz\",",
            "           \"integrity\": \"sha1-2Klr13/Wjfd5OnMDajug1UBdR3s=\",",
            "           \"dev\": true",
            "       }",
            "   }",
            "}"
        );

        ExecutableOutput executableOutput = createStandardOutput(
            "[",
            "  {",
            "    \"name\": \"@lerna/packageA\",",
            "    \"version\": \"1.2.3\",",
            "    \"private\": false,",
            "    \"location\": " + gson.toJson(getSourceDirectory() + "/packages/packageA"),
            "  },",
            "  {",
            "    \"name\": \"@lerna/packageB\",",
            "    \"version\": \"3.2.1\",",
            "    \"private\": true,",
            "    \"location\": " + gson.toJson(getSourceDirectory() + "/source/packages/packageB"),
            "  }",
            "]"
        );
        addExecutableOutput(executableOutput, "lerna", "ls", "--all", "--json");

        Path packagesDirectory = addDirectory(Paths.get("packages"));

        Path packageADirectory = addDirectory(packagesDirectory.resolve("packageA"));
        addPackageJson(packageADirectory, "packageA", "1.2.3",
            new NameVersion("brace-expansion", "~1")
        );

        Path packageBDirectory = addDirectory(packagesDirectory.resolve("packageB"));
        addPackageJson(packageBDirectory, "packageB", "3.2.1",
            new NameVersion("concat-map", "0.0.1")
        );

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
        NpmLockfileOptions npmLockFileOptions = new NpmLockfileOptions(true);
        YarnLockOptions yarnLockOptions = new YarnLockOptions(false, false, new ArrayList<>(0), new ArrayList<>(0));
        LernaOptions lernaOptions = new LernaOptions(false);

        return detectableFactory.createLernaDetectable(environment, () -> ExecutableTarget.forCommand("lerna"), npmLockFileOptions, yarnLockOptions, lernaOptions);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(2, extraction.getCodeLocations().size(), "Expected one code location from root, and one from a non-private package.");

        NameVersionGraphAssert rootGraphAssert = new NameVersionGraphAssert(Forge.NPMJS, extraction.getCodeLocations().get(0).getDependencyGraph());
        rootGraphAssert.hasRootSize(1);
        rootGraphAssert.hasRootDependency("concat-map", "0.0.1");

        NameVersionGraphAssert packageAGraphAssert = new NameVersionGraphAssert(Forge.NPMJS, extraction.getCodeLocations().get(1).getDependencyGraph());
        packageAGraphAssert.hasRootSize(1);
        packageAGraphAssert.hasRootDependency("brace-expansion", "1.1.8");
        packageAGraphAssert.hasDependency("balanced-match", "1.0.0");
        packageAGraphAssert.hasDependency("concat-map", "0.0.1");
        packageAGraphAssert.hasParentChildRelationship("brace-expansion", "1.1.8", "balanced-match", "1.0.0");
        packageAGraphAssert.hasParentChildRelationship("brace-expansion", "1.1.8", "concat-map", "0.0.1");
    }

}
