package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.detectable.detectables.bazel.model.Step;

public class GenerateJson {

    @Test
    public void generateScenario1() {
//        final Step step1 = new Step("executeBazelOnEach", Arrays.asList("query", "filter(\\\"@.*:jar\\\", deps(${detect.bazel.target}))"));
//        final Step step2 = new Step("edit", Arrays.asList("^@", ""));
//        final Step step3 = new Step("edit", Arrays.asList("//.*", ""));
//        final Step step4 = new Step("edit", Arrays.asList("^", "//external:"));
//        final Step step5 = new Step("executeBazelOnEach", Arrays.asList("query", "kind(maven_jar, ${0})", "--output", "xml"));
//        final Step step6 = new Step("parseEachXml", Arrays.asList("/query/rule[@class\\u003d\\u0027maven_jar\\u0027]/string[@name\\u003d\\u0027artifact\\u0027]", "value"));
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
    public void generateScenario2() {
        final Step step1 = new Step("executeBazelOnEach", Arrays.asList("cquery", "filter(\\\"@.*:jar\\\", deps(${detect.bazel.target}))"));
        final Step step2 = new Step("edit", Arrays.asList("^@", ""));
        final Step step3 = new Step("edit", Arrays.asList("//.*", ""));
        final Step step4 = new Step("edit", Arrays.asList("^", "//external:"));
        final Step step5 = new Step("executeBazelOnEach", Arrays.asList("cquery", "kind(maven_jar, ${0})", "--output", "textproto"));
        final Step step6 = new Step("parseEachTextProto", Arrays.asList(":results:target:rule:attribute", "artifact", "string_value"));
        final List<Step> steps = new ArrayList<>();
        steps.add(step1);
        steps.add(step2);
        steps.add(step3);
        steps.add(step4);
        steps.add(step5);
        steps.add(step6);

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String jsonString = gson.toJson(steps);
        System.out.println(jsonString);
    }

    @Test
    public void generateScenario3() throws IOException {
        final Step step1 = new Step("executeBazelOnEach", Arrays.asList("cquery", "deps(//:${detect.bazel.target})", "--output", "textproto"));
        final Step step2 = new Step("parseEachTextProto", Arrays.asList(":results:target:rule:attribute", "tags", "string_list_value"));
        final Step step3 = new Step("filter", Arrays.asList("^maven_coordinates="));
        final Step step4 = new Step("edit", Arrays.asList("^maven_coordinates=", ""));
        final List<Step> steps = new ArrayList<>();
        steps.add(step1);
        steps.add(step2);
        steps.add(step3);
        steps.add(step4);

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String jsonString = gson.toJson(steps);
        System.out.println(jsonString);
    }
}
