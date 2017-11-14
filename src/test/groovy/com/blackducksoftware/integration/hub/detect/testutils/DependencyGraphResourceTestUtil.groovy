package com.blackducksoftware.integration.hub.detect.testutils

import static org.junit.Assert.*

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.summary.DependencyGraphSummarizer
import com.blackducksoftware.integration.hub.bdio.graph.summary.GraphSummary
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class DependencyGraphResourceTestUtil {
    public static void assertGraph(String expectedResourceFile, DependencyGraph actualGraph) {
        DependencyGraphSummarizer summarizer = new DependencyGraphSummarizer(new Gson())

        TestUtil testUtil = new TestUtil()
        String json = testUtil.getResourceAsUTF8String(expectedResourceFile)

        GraphSummary expected = summarizer.fromJson(json)
        GraphSummary actual = summarizer.fromGraph(actualGraph)
        println new GsonBuilder().setPrettyPrinting().create().toJson(actual)
        assertSummarries(expected, actual)
    }

    public static void assertGraph(DependencyGraph expectedGraph, DependencyGraph actualGraph) {
        DependencyGraphSummarizer summarizer = new DependencyGraphSummarizer(new Gson())
        GraphSummary expected = summarizer.fromGraph(expectedGraph)
        GraphSummary actual = summarizer.fromGraph(actualGraph)
        println new GsonBuilder().setPrettyPrinting().create().toJson(actual)
        assertSummarries(expected, actual)
    }

    public static void assertSummarries(GraphSummary expected, GraphSummary actual) {
        assertSet(expected.rootExternalDataIds, actual.rootExternalDataIds, "Root external ids")
        assertSet(expected.dependencySummaries.keySet(), actual.dependencySummaries.keySet(), "Dependencies in graph")

        def expectedRelationshipIds = expected.externalDataIdRelationships.keySet()
        def expectedExistingRelationshipsIds = expectedRelationshipIds.findAll{ key -> expected.externalDataIdRelationships.get(key) != null && expected.externalDataIdRelationships.get(key).size() > 0}

        def actualRelationshipIds = actual.externalDataIdRelationships.keySet()
        def actualExistingRelationshipsIds = actualRelationshipIds.findAll{ key -> actual.externalDataIdRelationships.get(key) != null && actual.externalDataIdRelationships.get(key).size() > 0}

        assertSet(expectedExistingRelationshipsIds, actualExistingRelationshipsIds, "Existing relationships")

        for (String key : expected.dependencySummaries.keySet()){
            assertEquals(expected.dependencySummaries.get(key).name, actual.dependencySummaries.get(key).name)
            assertEquals(expected.dependencySummaries.get(key).version, actual.dependencySummaries.get(key).version)
        }
        for (String key : expectedExistingRelationshipsIds){
            assertSet(expected.externalDataIdRelationships.get(key), actual.externalDataIdRelationships.get(key), "External data id relationships for " + key)
        }
    }

    public static <T> void assertSet (Set<T> expected, Set<T> actual, String title) {
        Set<String> missingExpected = new HashSet<>(expected)
        missingExpected.removeAll(actual)

        Set<String> extraActual = new HashSet<>(actual)
        extraActual.removeAll(expected)

        assertTrue(title + ": Found missing expected " + missingExpected.toString(), missingExpected.size() == 0)
        assertTrue(title + ": Found extra actual " + extraActual.toString(), extraActual.size() == 0)
    }
}
