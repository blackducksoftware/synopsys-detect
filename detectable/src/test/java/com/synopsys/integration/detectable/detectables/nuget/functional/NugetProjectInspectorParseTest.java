package com.synopsys.integration.detectable.detectables.nuget.functional;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.projectinspector.ProjectInspectorParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class NugetProjectInspectorParseTest {
    @Test
    void checkParse() {
        String inspectorOutputPath = FunctionalTestFiles.resolvePath("/nuget/project_inspector/ConsoleApp.json");
        List<CodeLocation> codeLocations = new ProjectInspectorParser(new Gson(), new ExternalIdFactory()).parse(new File(inspectorOutputPath), false);

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

    @Test
    void checkParsingWithNoResults() {
        String inspectorOutputPath = FunctionalTestFiles.resolvePath("/nuget/project_inspector/ProjectInspectorNoResults.json");
        List<CodeLocation> codeLocations = new ProjectInspectorParser(new Gson(), new ExternalIdFactory()).parse(new File(inspectorOutputPath), false);

        Assertions.assertEquals(0, codeLocations.size());
    }
}
