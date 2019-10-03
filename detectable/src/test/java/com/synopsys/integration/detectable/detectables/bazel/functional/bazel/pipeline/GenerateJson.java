package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.detectable.detectables.bazel.model.Step;

public class GenerateJson {

    @Test
    public void generateMavenJarSteps() {
        final List<Step> steps = new ArrayList<>();
        steps.add(new Step("executeBazelOnEach", Arrays.asList("query", "filter('@.*:jar', deps(${detect.bazel.target}))")));
        steps.add(new Step("splitEach", Arrays.asList("\\s+")));
        steps.add(new Step("edit", Arrays.asList("^@", "")));
        steps.add(new Step("edit", Arrays.asList("//.*", "")));
        steps.add(new Step("edit", Arrays.asList("^", "//external:")));
        steps.add(new Step("executeBazelOnEach", Arrays.asList("query", "kind(maven_jar, ${0})", "--output", "xml")));
        steps.add(new Step("parseEachXml", Arrays.asList("/query/rule[@class='maven_jar']/string[@name='artifact']", "value")));

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String jsonString = gson.toJson(steps);
        System.out.println(jsonString);
    }

    @Test
    public void generateMavenInstallSteps() {
        final List<Step> steps = new ArrayList<>();
        steps.add(new Step("executeBazelOnEach", Arrays.asList("cquery", "--noimplicit_deps", "kind(j.*import, deps(${detect.bazel.target}))", "--output", "build")));
        steps.add(new Step("splitEach", Arrays.asList("\n")));
        steps.add(new Step("filter", Arrays.asList(".*maven_coordinates=.*")));
        steps.add(new Step("edit", Arrays.asList("^\\s*tags\\s*\\s*=\\s*\\[\\s*\"maven_coordinates=", "")));
        steps.add(new Step("edit", Arrays.asList("\".*", "")));

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String jsonString = gson.toJson(steps);
        System.out.println(jsonString);
    }
}
