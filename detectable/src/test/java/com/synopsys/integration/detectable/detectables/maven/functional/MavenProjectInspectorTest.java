package com.synopsys.integration.detectable.detectables.maven.functional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.projectinspector.ProjectInspectorOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;

public class MavenProjectInspectorTest extends DetectableFunctionalTest {

    public MavenProjectInspectorTest() throws IOException {
        super("nuget");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("pom.xml"));

        String source = getSourceDirectory().toFile().getPath();
        File jsonFile = new File(getOutputDirectory().toFile(), "inspection.json");
        String inspector = new File("inspector").getCanonicalPath();
        addExecutableOutput(createStandardOutput(""), inspector, "inspect", "--dir", source, "--output-file", jsonFile.getPath());

        addOutputFile(jsonFile.toPath(), FunctionalTestFiles.asListOfStrings("/maven/project_inspector_maven.json"));
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createMavenProjectInspectorDetectable(
            detectableEnvironment,
            () -> ExecutableTarget.forFile(new File("inspector")),
            new ProjectInspectorOptions(null, null, null)
        );
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        // TODO: Assert on all model fields or add unit tests to cover missing fields
        List<CodeLocation> codeLocations = extraction.getCodeLocations();

        Assertions.assertEquals(1, codeLocations.size());
        CodeLocation codeLocation = codeLocations.get(0);

        Set<Dependency> dependencies = codeLocation.getDependencyGraph().getRootDependencies();
        Assertions.assertEquals(1, dependencies.size());

        NameVersionGraphAssert dependencyGraph = new NameVersionGraphAssert(Forge.MAVEN, codeLocations.get(0).getDependencyGraph());

        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        ExternalId junit = externalIdFactory.createMavenExternalId("org.junit.jupiter", "junit-jupiter-api", "5.10.2");
        ExternalId apiguardian = externalIdFactory.createMavenExternalId("org.apiguardian","apiguardian-api","1.1.2");
        ExternalId opentest = externalIdFactory.createMavenExternalId("org.opentest4j","opentest4j","1.3.0");

        dependencyGraph.hasDependency(junit);
        dependencyGraph.hasDependency(apiguardian);

        dependencyGraph.hasParentChildRelationship(junit,opentest);
    }
}
