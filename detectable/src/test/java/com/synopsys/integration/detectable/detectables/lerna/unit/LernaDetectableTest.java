package com.synopsys.integration.detectable.detectables.lerna.unit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
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

public class LernaDetectableTest extends DetectableFunctionalTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public LernaDetectableTest() throws IOException {
        super("lerna");
    }

    @Override
    public void setup() throws IOException {
        addFile("lerna.json");

        addPackageJson(Paths.get(""), "lernaTest", "package-version",
            Collections.singletonList(new NameVersion("brace-expansion", "~1")),
            Collections.singletonList(new NameVersion("concat-map", "~1")),
            Collections.singletonList(new NameVersion("peer-example", "~1"))
        );

        addFile(
            Paths.get("package-lock.json"),
            "{",
            "   \"name\": \"lerna-project-name\",",
            "   \"version\": \"1.0.0\",",
            "   \"dependencies\": {",
            "       \"balanced-match\": {",
            "           \"version\": \"1.0.0\",",
            "           \"resolved\": \"https://registry.npmjs.org/balanced-match/-/balanced-match-1.0.0.tgz\",",
            "           \"integrity\": \"sha1-ibTRmasr7kneFk6gK4nORi1xt2c=\"",
            "       },",
            "       \"brace-expansion\": {",
            "           \"version\": \"1.1.8\",",
            "           \"resolved\": \"https://registry.npmjs.org/brace-expansion/-/brace-expansion-1.1.8.tgz\",",
            "           \"integrity\": \"sha1-wHshHHyVLsH479Uad+8NHTmQopI=\",",
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
            "       },",
            "       \"peer-example\": {",
            "           \"version\": \"1.0.0\",",
            "           \"resolved\": \"https://synopsys.com/404/peer-example-1.0.0.tgz\",",
            "           \"integrity\": \"sha1-5Hlr13/Wjfd5OnMDajug1UBdR3c=\",",
            "           \"peer\": true",
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
        addPackageJson(directory, packageName, packageVersion, Arrays.stream(dependencies).collect(Collectors.toList()), Collections.emptyList(), Collections.emptyList());
    }

    // TODO: Don't add the file to the test here. It's confusing
    private void addPackageJson(
        Path directory,
        String packageName,
        String packageVersion,
        List<NameVersion> dependencies,
        List<NameVersion> devDependencies,
        List<NameVersion> peerDependencies
    ) throws IOException {
        PackageJson packageJson = new PackageJson();
        packageJson.name = packageName;
        packageJson.version = packageVersion;
        packageJson.dependencies = dependencies.stream()
            .collect(Collectors.toMap(NameVersion::getName, NameVersion::getVersion));
        packageJson.devDependencies = devDependencies.stream()
            .collect(Collectors.toMap(NameVersion::getName, NameVersion::getVersion));
        packageJson.peerDependencies = peerDependencies.stream()
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
        Assertions.assertEquals(Extraction.ExtractionResultType.SUCCESS, extraction.getResult(), "Extraction should have been a success.");
        Assertions.assertEquals(2, extraction.getCodeLocations().size(), "Expected one code location from root, and one from a non-private package.");

        NameVersionGraphAssert rootGraphAssert = new NameVersionGraphAssert(Forge.NPMJS, extraction.getCodeLocations().get(0).getDependencyGraph());
        rootGraphAssert.hasRootSize(3);
        rootGraphAssert.hasRootDependency("brace-expansion", "1.1.8");
        rootGraphAssert.hasRootDependency("concat-map", "0.0.1");
        rootGraphAssert.hasRootDependency("peer-example", "1.0.0");

        NameVersionGraphAssert packageAGraphAssert = new NameVersionGraphAssert(Forge.NPMJS, extraction.getCodeLocations().get(1).getDependencyGraph());
        packageAGraphAssert.hasRootSize(1);
        packageAGraphAssert.hasRootDependency("brace-expansion", "1.1.8");
        packageAGraphAssert.hasDependency("balanced-match", "1.0.0");
        packageAGraphAssert.hasDependency("concat-map", "0.0.1");
        packageAGraphAssert.hasNoDependency("peer-example", "1.0.0");
        packageAGraphAssert.hasParentChildRelationship("brace-expansion", "1.1.8", "balanced-match", "1.0.0");
        packageAGraphAssert.hasParentChildRelationship("brace-expansion", "1.1.8", "concat-map", "0.0.1");
    }

}
