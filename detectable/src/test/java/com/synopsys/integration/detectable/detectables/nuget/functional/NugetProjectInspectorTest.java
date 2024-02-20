package com.synopsys.integration.detectable.detectables.nuget.functional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.projectinspector.ProjectInspectorOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class NugetProjectInspectorTest extends DetectableFunctionalTest {

    public NugetProjectInspectorTest() throws IOException {
        super("nuget");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("Example.csproj"));

        String source = getSourceDirectory().toFile().getPath();
        File jsonFile = new File(getOutputDirectory().toFile(), "inspection.json");
        String inspector = new File("inspector").getCanonicalPath();
        addExecutableOutput(createStandardOutput(""), inspector, "inspect", "--dir", source, "--output-file", jsonFile.getPath());

        addOutputFile(jsonFile.toPath(), FunctionalTestFiles.asListOfStrings("/nuget/project_inspector/ConsoleApp.json"));
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createNugetParseDetectable(
            detectableEnvironment,
            () -> ExecutableTarget.forFile(new File("inspector")),
            new ProjectInspectorOptions(null, null, null)
        );
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        List<CodeLocation> codeLocations = extraction.getCodeLocations();

        Assertions.assertEquals(2, codeLocations.size());
        Assertions.assertEquals(new File("/Users/devmehta/Desktop/p2p/p2pconn/FILE-0-myfile.csproj"), codeLocations.get(0).getSourcePath().orElse(null));
        Assertions.assertEquals(new File("/Users/devmehta/Desktop/p2p/p2pconn/p2pconn.csproj"), codeLocations.get(1).getSourcePath().orElse(null));

        NameVersionGraphAssert consoleApp1 = new NameVersionGraphAssert(Forge.NUGET, codeLocations.get(0).getDependencyGraph());
        consoleApp1.hasDependency("boost", "100.2.3");
        consoleApp1.hasDependency("MSTest.TestAdapter","1.0.0-preview");

        NameVersionGraphAssert consoleApp3 = new NameVersionGraphAssert(Forge.NUGET, codeLocations.get(1).getDependencyGraph());
        consoleApp3.hasDependency("Newtonsoft.Json", "13.0.1");
        consoleApp3.hasDependency("Newtonsoft.Json.Bson", "1.0.2");

        consoleApp3.hasParentChildRelationship("Newtonsoft.Json", "13.0.1", "Newtonsoft.Json.Bson", "1.0.2");
    }
}
