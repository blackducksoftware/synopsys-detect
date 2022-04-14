package com.synopsys.integration.detect.testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.BdioId;

// TODO: Remove this unused class? JM-04/2022
public class DependencyGraphResourceTestUtil {
    public static void assertGraph(String expectedResourceFile, DependencyGraph actualGraph) {
        DependencyGraphSummarizer summarizer = new DependencyGraphSummarizer(new Gson());

        TestUtil testUtil = new TestUtil();
        String json = testUtil.getResourceAsUTF8String(expectedResourceFile);

        GraphSummary expected = summarizer.fromJson(json);
        GraphSummary actual = summarizer.fromGraph(actualGraph);
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(actual));
        assertSummaries(expected, actual);
    }

    public static void assertGraph(DependencyGraph expectedGraph, DependencyGraph actualGraph) {
        DependencyGraphSummarizer summarizer = new DependencyGraphSummarizer(new Gson());
        GraphSummary expected = summarizer.fromGraph(expectedGraph);
        GraphSummary actual = summarizer.fromGraph(actualGraph);
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(actual));
        assertSummaries(expected, actual);
    }

    public static void assertSummaries(GraphSummary expected, GraphSummary actual) {
        assertSet(expected.rootExternalDataIds, actual.rootExternalDataIds, "Root external ids");
        assertSet(expected.dependencySummaries.keySet(), actual.dependencySummaries.keySet(), "Dependencies in graph");

        Set<BdioId> expectedRelationshipIds = expected.externalDataIdRelationships.keySet();
        Set<BdioId> expectedExistingRelationshipsIds = expectedRelationshipIds.stream()
            .filter(key -> expected.externalDataIdRelationships.get(key) != null && expected.externalDataIdRelationships.get(key).size() > 0)
            .collect(Collectors.toSet());

        Set<BdioId> actualRelationshipIds = actual.externalDataIdRelationships.keySet();
        Set<BdioId> actualExistingRelationshipsIds = actualRelationshipIds.stream()
            .filter(key -> actual.externalDataIdRelationships.get(key) != null && actual.externalDataIdRelationships.get(key).size() > 0)
            .collect(Collectors.toSet());

        assertSet(expectedExistingRelationshipsIds, actualExistingRelationshipsIds, "Existing relationships");

        for (BdioId key : expected.dependencySummaries.keySet()) {
            assertEquals(expected.dependencySummaries.get(key).getName(), actual.dependencySummaries.get(key).getName());
            assertEquals(expected.dependencySummaries.get(key).getVersion(), actual.dependencySummaries.get(key).getVersion());
        }
        for (BdioId key : expectedExistingRelationshipsIds) {
            assertSet(expected.externalDataIdRelationships.get(key), actual.externalDataIdRelationships.get(key), "External data id relationships for " + key);
        }
    }

    public static <T> void assertSet(Set<T> expected, Set<T> actual, String title) {
        Set<T> missingExpected = new HashSet<>(expected);
        missingExpected.removeAll(actual);

        Set<T> extraActual = new HashSet<>(actual);
        extraActual.removeAll(expected);

        assertEquals(0, missingExpected.size(), title + ": Found missing expected " + missingExpected);
        assertEquals(0, extraActual.size(), title + ": Found extra actual " + extraActual);
    }
}
