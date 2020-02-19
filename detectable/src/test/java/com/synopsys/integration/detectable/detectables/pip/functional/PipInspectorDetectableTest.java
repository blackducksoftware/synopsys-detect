package com.synopsys.integration.detectable.detectables.pip.functional;

import java.io.File;
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
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectables.pip.PipInspectorDetectableOptions;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class PipInspectorDetectableTest extends DetectableFunctionalTest {
    private final static String PYTHON_CMD = "python";
    private final static String PIP_CMD = "pip";
    private final static String PIP_INSPECTOR_CMD = "/pip-inspector";

    protected PipInspectorDetectableTest() throws IOException {
        super("pip-inspector");
    }

    @Override
    protected void setup() throws IOException {
        final Path setupFilePath = addFile("setup.py");

        addExecutableOutput(createStandardOutput("project-name"), PYTHON_CMD, setupFilePath.toAbsolutePath().toString(), "--name");

        addExecutableOutput(createStandardOutput(
            "project-name==project-version",
            "    dep1==1.0",
            "        dep12==2.0",
            "    dep2==3.0"
        ), PYTHON_CMD, PIP_INSPECTOR_CMD, "--projectname=project-name");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull final DetectableEnvironment detectableEnvironment) {
        final List<Path> requirementTxtPaths = new ArrayList<>();
        final PipInspectorDetectableOptions pipInspectorDetectableOptions = new PipInspectorDetectableOptions("project-name", requirementTxtPaths);
        return detectableFactory.createPipInspectorDetectable(detectableEnvironment, pipInspectorDetectableOptions, () -> new File(PIP_INSPECTOR_CMD), () -> new File(PYTHON_CMD), () -> new File(PIP_CMD));
    }

    @Override
    public void assertExtraction(@NotNull final Extraction extraction) {
        Assertions.assertEquals("project-name", extraction.getProjectName());
        Assertions.assertEquals("project-version", extraction.getProjectVersion());
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        final DependencyGraph dependencyGraph = extraction.getCodeLocations().get(0).getDependencyGraph();
        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, dependencyGraph);

        graphAssert.hasNoDependency("project-name", "project-version");
        graphAssert.hasRootDependency("dep1", "1.0");
        graphAssert.hasRootDependency("dep2", "3.0");
        graphAssert.hasParentChildRelationship("dep1", "1.0", "dep12", "2.0");
    }
}
