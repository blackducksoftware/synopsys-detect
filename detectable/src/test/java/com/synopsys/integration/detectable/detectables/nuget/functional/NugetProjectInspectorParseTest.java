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
        List<CodeLocation> codeLocations = new ProjectInspectorParser(new Gson(), new ExternalIdFactory()).parse(new File(inspectorOutputPath));

        Assertions.assertEquals(2, codeLocations.size());
        Assertions.assertEquals(new File("C:\\Users\\jordanp\\source\\repos\\ConsoleApp3\\ConsoleApp1\\ConsoleApp1.csproj"), codeLocations.get(0).getSourcePath().orElse(null));
        Assertions.assertEquals(new File("C:\\Users\\jordanp\\source\\repos\\ConsoleApp3\\ConsoleApp3\\ConsoleApp3.csproj"), codeLocations.get(1).getSourcePath().orElse(null));

        NameVersionGraphAssert consoleApp1 = new NameVersionGraphAssert(Forge.NUGET, codeLocations.get(0).getDependencyGraph());
        consoleApp1.hasDependency("DocumentFormat.OpenXml", "2.13.0");

        NameVersionGraphAssert consoleApp3 = new NameVersionGraphAssert(Forge.NUGET, codeLocations.get(1).getDependencyGraph());
        consoleApp3.hasDependency("Newtonsoft.Json.Bson", "1.0.2");
        consoleApp3.hasDependency("Serilog", "2.10.0");

        consoleApp3.hasParentChildRelationship("Newtonsoft.Json.Bson", "1.0.2", "Newtonsoft.Json", "12.0.1");
    }

    @Test
    void checkParsingWithNoResults() {
        String inspectorOutputPath = FunctionalTestFiles.resolvePath("/nuget/project_inspector/ProjectInspectorNoResults.json");
        List<CodeLocation> codeLocations = new ProjectInspectorParser(new Gson(), new ExternalIdFactory()).parse(new File(inspectorOutputPath));

        Assertions.assertEquals(0, codeLocations.size());
    }
}
