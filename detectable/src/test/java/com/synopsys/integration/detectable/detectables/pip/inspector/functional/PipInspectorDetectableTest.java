package com.synopsys.integration.detectable.detectables.pip.inspector.functional;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectables.pip.inspector.PipInspectorDetectableOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class PipInspectorDetectableTest extends DetectableFunctionalTest {
    private final static String PYTHON_CMD = "python";
    private final static String PIP_CMD = "pip";

    Path pipInspectorPath;

    protected PipInspectorDetectableTest() throws IOException {
        super("pip-inspector");
    }

    @Override
    protected void setup() throws IOException {
        pipInspectorPath = addOutputFile("pip-inspector");
        Path setupFilePath = addFile("setup.py");

        addExecutableOutput(createStandardOutput("project-name"), PYTHON_CMD, setupFilePath.toAbsolutePath().toString(), "--name");

        addExecutableOutput(createStandardOutput(
            "project-name==project-version",
            "    dep1==1.0",
            "        dep12==2.0",
            "    dep2==3.0"
        ), PYTHON_CMD, pipInspectorPath.toString(), "--projectname=project-name");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        List<Path> requirementTxtPaths = new ArrayList<>();
        PipInspectorDetectableOptions pipInspectorDetectableOptions = new PipInspectorDetectableOptions("project-name", requirementTxtPaths);
        return detectableFactory.createPipInspectorDetectable(
            detectableEnvironment,
            pipInspectorDetectableOptions,
            () -> pipInspectorPath.toFile(),
            () -> ExecutableTarget.forCommand(PYTHON_CMD),
            () -> ExecutableTarget.forCommand(PIP_CMD)
        );
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals("project-name", extraction.getProjectName());
        Assertions.assertEquals("project-version", extraction.getProjectVersion());
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        DependencyGraph dependencyGraph = extraction.getCodeLocations().get(0).getDependencyGraph();
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, dependencyGraph);

        graphAssert.hasNoDependency("project-name", "project-version");
        graphAssert.hasRootDependency("dep1", "1.0");
        graphAssert.hasRootDependency("dep2", "3.0");
        graphAssert.hasParentChildRelationship("dep1", "1.0", "dep12", "2.0");
    }
}
