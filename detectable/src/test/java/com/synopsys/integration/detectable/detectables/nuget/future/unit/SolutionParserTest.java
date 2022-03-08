package com.synopsys.integration.detectable.detectables.nuget.future.unit;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.nuget.future.ParsedProject;
import com.synopsys.integration.detectable.detectables.nuget.future.SolutionParser;

public class SolutionParserTest {
    @Test
    void CanParseProject() {
        List<String> solutionLines = Arrays.asList(
            "Project(\"{9A19103F-16F7-4668-BE54-9A1E7A4F7556}\") = \"NugetDotnet5Inspector\", \"NugetDotnet5Inspector\\NugetDotnet5Inspector.csproj\", \"{AEDCF063-2341-465D-B256-39125610E39B}\""
            ,
            "EndProject");

        SolutionParser solutionParser = new SolutionParser();
        List<ParsedProject> projects = solutionParser.projectsFromSolution(solutionLines);

        Assertions.assertEquals(1, projects.size());
        Assertions.assertEquals("NugetDotnet5Inspector\\NugetDotnet5Inspector.csproj", projects.get(0).getPath());
    }
}
