package com.synopsys.integration.detectable.detectables.nuget.future.functional;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.nuget.future.ParsedProject;
import com.synopsys.integration.detectable.detectables.nuget.future.SolutionParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

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
