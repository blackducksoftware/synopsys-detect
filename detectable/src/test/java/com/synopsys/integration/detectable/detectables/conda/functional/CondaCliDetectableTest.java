package com.synopsys.integration.detectable.detectables.conda.functional;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.CondaResolver;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectableOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.executable.ExecutableOutput;

public class CondaCliDetectableTest extends DetectableFunctionalTest {

    public CondaCliDetectableTest() throws IOException {
        super("conda");
    }

    @Override
    protected void setup() throws IOException {
        addFile("environment.yml");

        ExecutableOutput condaListOutput = createStandardOutput(
            "[",
            "   {",
            "       \"base_url\": null,",
            "       \"build_number\": 0,",
            "       \"build_string\": \"0\",",
            "       \"channel\": \"defaults\",",
            "       \"dist_name\": \"mkl-2017.0.3-0\",",
            "       \"name\": \"mkl\",",
            "       \"platform\": null,",
            "       \"version\": \"2017.0.3\",",
            "       \"with_features_depends\": null",
            "   },",
            "   {",
            "       \"base_url\": null,",
            "       \"build_number\": 0,",
            "       \"build_string\": \"py36_0\",",
            "       \"channel\": \"defaults\",",
            "       \"dist_name\": \"numpy-1.13.1-py36_0\",",
            "       \"name\": \"numpy\",",
            "       \"platform\": null,",
            "       \"version\": \"1.13.1\",",
            "       \"with_features_depends\": null",
            "   }",
            "]"
        );
        addExecutableOutput(condaListOutput, "conda", "list", "-n", "conda-env", "--json");

        ExecutableOutput condaInfoOutput = createStandardOutput(
            "{",
            "   \"conda_build_version\": \"not installed\",",
            "   \"conda_env_version\": \"4.3.22\",",
            "   \"conda_location\": \"/usr/local/miniconda3/lib/python3.6/site-packages/conda\",",
            "   \"conda_prefix\": \"/usr/local/miniconda3\",",
            "   \"conda_private\": false,",
            "   \"conda_version\": \"4.3.22\",",
            "   \"default_prefix\": \"/usr/local/miniconda3\",",
            "   \"platform\": \"osx-64\"",
            "}"
        );
        addExecutableOutput(getOutputDirectory(), condaInfoOutput, "conda", "info", "--json");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        class CondaResolverTest implements CondaResolver {

            @Override
            public ExecutableTarget resolveConda() throws DetectableException {
                return ExecutableTarget.forCommand("conda");
            }
        }
        return detectableFactory.createCondaCliDetectable(detectableEnvironment, new CondaResolverTest(), new CondaCliDetectableOptions("conda-env"));
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.ANACONDA, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("mkl", "2017.0.3-0-osx-64");
        graphAssert.hasRootDependency("numpy", "1.13.1-py36_0-osx-64");

    }
}
