package com.synopsys.integration.detectable.detectables.maven.functional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

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

        addOutputFile(jsonFile.toPath(), "{",
            "   \"Dir\": \"/opt/project/src\",",
            "   \"Modules\": {",
            "      \"/opt/project/src/pom.xml\": {",
            "         \"ModuleFile\": \"/opt/project/src/pom.xml\",",
            "         \"ModuleDir\": \"/opt/project/src\",",
            "         \"Dependencies\": [",
            "            {",
            "               \"Id\": \"91390d46-4824-1909-fc05-0d949a4466c8\",",
            "               \"IncludedBy\": [",
            "                  \"DIRECT\"",
            "               ],",
            "               \"MavenCoordinates\": {",
            "                  \"GroupId\": \"COORDINATE_GROUP\",",
            "                  \"ArtifactId\": \"COORDINATE_ARTIFACT\",",
            "                  \"Version\": \"COORDINATE_VERSION\"",
            "               },",
            "               \"DependencyType\": \"MAVEN\",",
            "               \"DependencySource\": \"EXTERNAL\",",
            "               \"Name\": \"NON_COORDINATE_NAME\",",
            "               \"Version\": \"NON_COORDINATE_VERSION\",",
            "               \"Artifacts\": [",
            "                  \"/root/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar\"",
            "               ],",
            "               \"Scope\": \"compile\"",
            "            }",
            "         ],",
            "         \"Strategy\": \"MAVEN\"",
            "      }",
            "   }",
            "}"
        );
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

        Dependency first = dependencies.iterator().next();
        Assertions.assertNotNull(first);

        Assertions.assertEquals("COORDINATE_ARTIFACT", first.getName());
        Assertions.assertEquals("COORDINATE_VERSION", first.getVersion());

        ExternalId firstId = first.getExternalId();
        Assertions.assertEquals("COORDINATE_ARTIFACT", firstId.getName());
        Assertions.assertEquals("COORDINATE_VERSION", firstId.getVersion());
        Assertions.assertEquals("COORDINATE_GROUP", firstId.getGroup());
    }
}
