package com.synopsys.integration.detect.battery.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.SetUtils;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.skyscreamer.jsonassert.JSONParser;

import com.google.gson.Gson;
import com.synopsys.integration.detect.battery.util.bdio2.Bdio2Keys;
import com.synopsys.integration.detect.battery.util.bdio2.Bdio2Node;

public class Bdio2Compare {
    Gson gson = new Gson();

    public static List<BdioIssue> compare(File actualDirectory, File expectedDirectory) throws JSONException, IOException {
        File[] actualChunks = actualDirectory.listFiles();
        File[] expectedChunks = expectedDirectory.listFiles();
        Assertions.assertNotNull(actualChunks);
        Assertions.assertNotNull(expectedChunks);

        Assertions.assertEquals(2, expectedChunks.length);
        Assertions.assertEquals(2, actualChunks.length);

        assertHeaders(actualDirectory, expectedDirectory);
        return assertEntry(actualDirectory, expectedDirectory);
    }

    static List<BdioIssue> assertEntry(File actualDirectory, File expectedDirectory) throws IOException, JSONException {
        File expectedHeader = new File(expectedDirectory, "bdio-entry-00.jsonld");
        File actualHeader = new File(actualDirectory, "bdio-entry-00.jsonld");

        String expectedJsonStr = FileUtils.readFileToString(expectedHeader, Charset.defaultCharset());
        String actualJsonStr = FileUtils.readFileToString(actualHeader, Charset.defaultCharset());

        JSONObject expectedJson = (JSONObject) JSONParser.parseJSON(expectedJsonStr);
        JSONObject actualJson = (JSONObject) JSONParser.parseJSON(actualJsonStr);

        JSONArray expectedGraph = expectedJson.getJSONArray("@graph");
        JSONArray actualGraph = actualJson.getJSONArray("@graph");

        List<Bdio2Node> expectedNodes = parseNodes(expectedGraph);
        List<Bdio2Node> actualNodes = parseNodes(actualGraph);

        List<BdioIssue> issues = new ArrayList<>();
        issues.addAll(assertIds(actualNodes, expectedNodes));
        issues.addAll(assertRelationships(actualNodes, expectedNodes));
        return issues;
    }

    static List<Bdio2Node> parseNodes(JSONArray array) throws JSONException {
        List<Bdio2Node> nodes = new ArrayList<>();
        for (int r = 0; r < array.length(); r++) {
            JSONObject obj = array.getJSONObject(r);
            Bdio2Node node = new Bdio2Node(obj);
            nodes.add(node);
        }
        return nodes;
    }

    static List<BdioIssue> assertRelationships(List<Bdio2Node> actual, List<Bdio2Node> expected) {
        List<BdioIssue> issues = new ArrayList<>();
        for (Bdio2Node expectedNode : expected) {
            for (Bdio2Node actualNode : actual) {
                if (expectedNode.getId().equals(actualNode.getId())) {
                    Set<String> differenceRelated = SetUtils.disjunction(new HashSet<>(expectedNode.getDependencies()), new HashSet<>(actualNode.getDependencies()));
                    for (String difference : differenceRelated) {
                        if (expectedNode.getDependencies().contains(difference)) {
                            issues.add(new BdioIssue(
                                "There was a missing relationship (in expected but not actual) in the component " + expectedNode.toDescription() + " to the component "
                                    + firstComponent(difference, expected, actual).toDescription() + "."));
                        } else if (actualNode.getDependencies().contains(difference)) {
                            issues.add(new BdioIssue(
                                "There was an additional relationship (in actual but not in expected) in the " +
                                    "component " + expectedNode.toDescription() + " to the component " + firstComponent(difference, expected, actual).toDescription()
                                    + "."));
                        } else {
                            throw new RuntimeException("Something went wrong comparing BDIO. An related id was " +
                                "different in actual and in expected BDIO nodes, but neither actually had the relationship.");
                        }
                    }
                }
            }
        }
        return issues;
    }

    static Bdio2Node firstComponent(String id, List<Bdio2Node> expected, List<Bdio2Node> actual) {
        Optional<Bdio2Node> expectedNode = expected.stream().filter(it -> it.getId().equals(id)).findFirst();
        if (expectedNode.isPresent()) {
            return expectedNode.get();
        }
        Optional<Bdio2Node> actualNode = actual.stream().filter(it -> it.getId().equals(id)).findFirst();
        if (actualNode.isPresent()) {
            return actualNode.get();
        }
        throw new RuntimeException("Something went wrong comparing BDIO. Could not find component id in actual or expected: " + actual);
    }

    static List<BdioIssue> assertIds(List<Bdio2Node> actual, List<Bdio2Node> expected) {
        List<BdioIssue> issues = new ArrayList<>();

        Set<String> actualIds = actual.stream().map(Bdio2Node::getId).collect(Collectors.toSet());
        Set<String> expectedIds = expected.stream().map(Bdio2Node::getId).collect(Collectors.toSet());
        Set<String> differentIds = SetUtils.disjunction(actualIds, expectedIds);

        for (String differentId : differentIds) { //could maybe use assertion set utils but its logic is different enough to make it tricky... -jp
            Optional<Bdio2Node> expectedNode = expected.stream().filter(it -> it.getId().equals(differentId)).findFirst();
            if (expectedNode.isPresent()) {
                String expectedNodeType = expectedNode.get().getType().split("#")[1];
                issues.add(new BdioIssue("An expected " + expectedNodeType + " node was not found in the bdio " + expectedNode.get().toDescription() + "."));
            } else {
                Optional<Bdio2Node> actualNode = actual.stream().filter(it -> it.getId().equals(differentId)).findFirst();
                if (actualNode.isPresent()) {
                    String actualNodeType = actualNode.get().getType().split("#")[1];
                    issues.add(new BdioIssue("An additional " + actualNodeType + " node was found in the bdio " + actualNode.get().toDescription() + "."));
                } else {
                    throw new RuntimeException("Something went wrong comparing BDIO. An id was different in " +
                        "actual and in expected but neither actually had the node.");
                }
            }
        }

        return issues;
    }

    static void assertHeaders(File actualDirectory, File expectedDirectory) throws IOException, JSONException {
        File expectedHeader = new File(expectedDirectory, "bdio-header.jsonld");
        File actualHeader = new File(actualDirectory, "bdio-header.jsonld");

        String expectedJsonStr = FileUtils.readFileToString(expectedHeader, Charset.defaultCharset());
        String actualJsonStr = FileUtils.readFileToString(actualHeader, Charset.defaultCharset());

        JSONObject expectedJson = (JSONObject) JSONParser.parseJSON(expectedJsonStr);
        JSONObject actualJson = (JSONObject) JSONParser.parseJSON(actualJsonStr);

        Assertions.assertEquals(expectedJson.getString(Bdio2Keys.hasName), actualJson.getString(Bdio2Keys.hasName));
        Assertions.assertEquals(expectedJson.getString(Bdio2Keys.hasProject), actualJson.getString(Bdio2Keys.hasProject));
        Assertions.assertEquals(expectedJson.getString(Bdio2Keys.hasProjectVersion), actualJson.getString(Bdio2Keys.hasProjectVersion));
    }
}
