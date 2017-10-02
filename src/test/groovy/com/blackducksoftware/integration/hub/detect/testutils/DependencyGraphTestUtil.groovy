package com.blackducksoftware.integration.hub.detect.testutils

import org.junit.Assert

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.summary.DependencyGraphSummarizer
import com.blackducksoftware.integration.hub.bdio.graph.summary.GraphSummary
import com.google.gson.Gson

class DependencyGraphTestUtil {

    public static void assertGraph(String expectedResourceFile, DependencyGraph actualGraph) {
        DependencyGraphSummarizer summarizer = new DependencyGraphSummarizer(new Gson());

        TestUtil testUtil = new TestUtil();
        String json = testUtil.getResourceAsUTF8String(expectedResourceFile)

        GraphSummary expected = summarizer.fromJson(json);
        GraphSummary actual = summarizer.fromGraph(actualGraph)
        assertSummarries(expected, actual);
    }


    public static void assertSummarries(GraphSummary expected, GraphSummary actual) {

        assertSet(expected.rootExternalDataIds, actual.rootExternalDataIds);
        assertSet(expected.dependencySummaries.keySet(), actual.dependencySummaries.keySet());

        def expectedRelationshipIds = expected.externalDataIdRelationships.keySet()
        def expectedExistingRelationshipsIds = expectedRelationshipIds.findAll{ key -> expected.externalDataIdRelationships.get(key) != null && expected.externalDataIdRelationships.get(key).size() > 0}

        def actualRelationshipIds = actual.externalDataIdRelationships.keySet()
        def actualExistingRelationshipsIds = actualRelationshipIds.findAll{ key -> actual.externalDataIdRelationships.get(key) != null && actual.externalDataIdRelationships.get(key).size() > 0}

        assertSet(expectedExistingRelationshipsIds, actualExistingRelationshipsIds);

        for (String key : expected.dependencySummaries.keySet()){
            Assert.assertEquals(expected.dependencySummaries.get(key).name, actual.dependencySummaries.get(key).name);
            Assert.assertEquals(expected.dependencySummaries.get(key).version, actual.dependencySummaries.get(key).version);
        }
        for (String key : expectedExistingRelationshipsIds){
            assertSet(expected.externalDataIdRelationships.get(key), actual.externalDataIdRelationships.get(key));
        }
    }

    public static <T> void assertSet (Set<T> expected, Set<T> actual) {
        Set<String> missingExpected = new HashSet<>(expected);
        missingExpected.removeAll(actual)

        Set<String> extraActual = new HashSet<>(actual);
        extraActual.removeAll(expected)

        Assert.assertEquals(0, missingExpected.size());
        Assert.assertEquals(0, extraActual.size());
    }
}
