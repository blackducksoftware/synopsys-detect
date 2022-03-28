package com.synopsys.integration.detectable.detectables.pip.inspector.functional;

import java.io.IOException;
import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectables.pipenv.build.PipenvDetectableOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class PipEnvDetectableTest extends DetectableFunctionalTest {
    private final static String PYTHON_CMD = "python";
    private final static String PIPENV_CMD = "pipenv";

    protected PipEnvDetectableTest() throws IOException {
        super("pipenv");
    }

    @Override
    protected void setup() throws IOException {
        addFile("Pipfile");
        addFile("Pipfile.lock");
        Path setupFilePath = addFile("setup.py");

        addExecutableOutput(createStandardOutput("project-name"), PYTHON_CMD, setupFilePath.toAbsolutePath().toString(), "--name");

        addExecutableOutput(createStandardOutput("version-name"), PYTHON_CMD, setupFilePath.toAbsolutePath().toString(), "--version");

        addExecutableOutput(createStandardOutput(
            "simple==1",
            "with-dashes==2.0",
            "dots.and-dashes==3.1.2"
        ), PIPENV_CMD, "run", "pip", "freeze");

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
        ), PIPENV_CMD, "graph", "--bare", "--json-tree");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        PipenvDetectableOptions pipenvDetectableOptions = new PipenvDetectableOptions("simple", "1", false);
        return detectableFactory.createPipenvDetectable(
            detectableEnvironment,
            pipenvDetectableOptions,
            () -> ExecutableTarget.forCommand(PYTHON_CMD),
            () -> ExecutableTarget.forCommand(PIPENV_CMD)
        );
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals("simple", extraction.getProjectName());
        Assertions.assertEquals("1", extraction.getProjectVersion());
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        DependencyGraph dependencyGraph = extraction.getCodeLocations().get(0).getDependencyGraph();
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, dependencyGraph);

        graphAssert.hasNoDependency("simple", "1");
        graphAssert.hasRootDependency("with-dashes", "2.0");
        graphAssert.hasRootDependency("dots.and-dashes", "3.1.2");
        graphAssert.hasParentChildRelationship("with-dashes", "2.0", "dots.and-dashes", "3.1.2");
    }
}
