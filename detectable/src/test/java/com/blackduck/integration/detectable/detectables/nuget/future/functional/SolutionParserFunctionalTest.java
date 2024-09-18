package com.blackduck.integration.detectable.detectables.nuget.future.functional;

import java.util.List;

import com.blackduck.integration.detectable.detectables.nuget.future.ParsedProject;
import com.blackduck.integration.detectable.detectables.nuget.future.SolutionParser;
import com.blackduck.integration.detectable.util.FunctionalTestFiles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SolutionParserFunctionalTest {
    @Test
    void CanParseProject() {
        List<String> solutionLines = FunctionalTestFiles.asListOfStrings("/nuget/future/NugetDotnet5Inspector.sln");

        SolutionParser solutionParser = new SolutionParser();
        List<ParsedProject> projects = solutionParser.projectsFromSolution(solutionLines);

        Assertions.assertEquals(2, projects.size());
        Assertions.assertEquals("NugetDotnet5Inspector\\NugetDotnet5Inspector.csproj", projects.get(0).getPath());
        Assertions.assertEquals("NugetDotnet5InspectorTests\\NugetDotnet5InspectorTests.csproj", projects.get(1).getPath());
    }
}
