package com.synopsys.integration.detectable.detectables.pip.functional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipenvResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PythonResolver;
import com.synopsys.integration.detectable.detectables.pip.PipenvDetectableOptions;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class PipEnvDetectableTest extends DetectableFunctionalTest {
    protected PipEnvDetectableTest() throws IOException {
        super("pipenv");
    }

    @Override
    protected void setup() throws IOException {
        addFile("Pipfile");
        addFile("Pipfile.lock");
        final Path setupFilePath = addFile("setup.py");

        addExecutableOutput(createStandardOutput("project-name"), "python", setupFilePath.toAbsolutePath().toString(), "--name");

        addExecutableOutput(createStandardOutput("version-name"), "python", setupFilePath.toAbsolutePath().toString(), "--version");

        addExecutableOutput(createStandardOutput(
            "simple==1",
            "with-dashes==2.0",
            "dots.and-dashes==3.1.2"
        ), "pipenv", "run", "pip", "freeze");

        addExecutableOutput(createStandardOutput(
            "[",
            "    {",
            "        \"key\": \"simple\",",
            "        \"package_name\": \"simple\",",
            "        \"installed_version\": \"1\",",
            "        \"required_version\": \"1\",",
            "        \"dependencies\": [",
            "            {",
            "                \"key\": \"with-dashes\",",
            "                \"package_name\": \"with-dashes\",",
            "                \"installed_version\": \"2.0\",",
            "                \"required_version\": \">=2.0\",",
            "                \"dependencies\": [",
            "                    {",
            "                        \"key\": \"dots.and-dashes==3.1.2\",",
            "                        \"package_name\": \"dots.and-dashes\",",
            "                        \"installed_version\": \"3.1.2\",",
            "                        \"required_version\": \">=3.1.0\",",
            "                        \"dependencies\": []",
            "                    }",
            "                ]",
            "            },",
            "            {",
            "                \"key\": \"dots.and-dashes==3.1.2\",",
            "                \"package_name\": \"dots.and-dashes\",",
            "                \"installed_version\": \"3.1.2\",",
            "                \"required_version\": \">=3.1.0\",",
            "                \"dependencies\": []",
            "            }",
            "        ]",
            "    },",
            "]"
        ), "pipenv", "graph", "--bare", "--json-tree");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull final DetectableEnvironment detectableEnvironment) {
        final PipenvDetectableOptions pipenvDetectableOptions = new PipenvDetectableOptions("simple", "1", false);
        return detectableFactory.createPipenvDetectable(detectableEnvironment, pipenvDetectableOptions, new TestPythonResolver(), new TestPipResolver());
    }

    @Override
    public void assertExtraction(@NotNull final Extraction extraction) {
        Assertions.assertEquals("simple", extraction.getProjectName());
        Assertions.assertEquals("1", extraction.getProjectVersion());
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        final DependencyGraph dependencyGraph = extraction.getCodeLocations().get(0).getDependencyGraph();
        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, dependencyGraph);

        graphAssert.hasNoDependency("simple", "1");
        graphAssert.hasRootDependency("with-dashes", "2.0");
        graphAssert.hasRootDependency("dots.and-dashes", "3.1.2");
        graphAssert.hasParentChildRelationship("with-dashes", "2.0", "dots.and-dashes", "3.1.2");
    }

    private static class TestPythonResolver implements PythonResolver {
        @Override
        public File resolvePython() {
            return new File("python");
        }
    }

    private static class TestPipResolver implements PipenvResolver {
        @Override
        public File resolvePipenv() {
            return new File("pipenv");
        }
    }
}
