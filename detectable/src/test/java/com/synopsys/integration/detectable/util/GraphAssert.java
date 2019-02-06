package com.synopsys.integration.detectable.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.summary.DependencyGraphSummarizer;
import com.synopsys.integration.bdio.graph.summary.GraphSummary;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class GraphAssert {
    private final Forge forge;
    private final DependencyGraph graph;
    private final ExternalIdFactory externalIdFactory;

    public GraphAssert(final Forge forge, final DependencyGraph graph) {
        this.forge = forge;
        this.graph = graph;
        this.externalIdFactory = new ExternalIdFactory();
    }

    public static void assertGraph(final String expectedResourceFile, final DependencyGraph actualGraph) {
        final DependencyGraphSummarizer summarizer = new DependencyGraphSummarizer(new Gson());

        final String json = FunctionalTestFiles.asString(expectedResourceFile);

        final GraphSummary expected = summarizer.fromJson(json);
        final GraphSummary actual = summarizer.fromGraph(actualGraph);
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(actual));
        assertSummarries(expected, actual);
    }

    public static void assertGraph(final DependencyGraph expectedGraph, final DependencyGraph actualGraph) {
        final DependencyGraphSummarizer summarizer = new DependencyGraphSummarizer(new Gson());
        final GraphSummary expected = summarizer.fromGraph(expectedGraph);
        final GraphSummary actual = summarizer.fromGraph(actualGraph);
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(actual));
        assertSummarries(expected, actual);
    }

    public static void assertSummarries(final GraphSummary expected, final GraphSummary actual) {
        assertSet(expected.rootExternalDataIds, actual.rootExternalDataIds, "Root external ids");
        assertSet(expected.dependencySummaries.keySet(), actual.dependencySummaries.keySet(), "Dependencies in graph");

        final Set<String> expectedRelationshipIds = expected.externalDataIdRelationships.keySet();
        final Set<String> expectedExistingRelationshipsIds = expectedRelationshipIds.stream().filter(key -> expected.externalDataIdRelationships.get(key) != null && expected.externalDataIdRelationships.get(key).size() > 0)
                                                                 .collect(Collectors.toSet());

        final Set<String> actualRelationshipIds = actual.externalDataIdRelationships.keySet();
        final Set<String> actualExistingRelationshipsIds = actualRelationshipIds.stream().filter(key -> actual.externalDataIdRelationships.get(key) != null && actual.externalDataIdRelationships.get(key).size() > 0)
                                                               .collect(Collectors.toSet());

        assertSet(expectedExistingRelationshipsIds, actualExistingRelationshipsIds, "Existing relationships");

        for (final String key : expected.dependencySummaries.keySet()) {
            assertEquals(expected.dependencySummaries.get(key).getName(), actual.dependencySummaries.get(key).getName());
            assertEquals(expected.dependencySummaries.get(key).getVersion(), actual.dependencySummaries.get(key).getVersion());
        }
        for (final String key : expectedExistingRelationshipsIds) {
            assertSet(expected.externalDataIdRelationships.get(key), actual.externalDataIdRelationships.get(key), "External data id relationships for " + key);
        }
    }

    public static <T> void assertSet(final Set<T> expected, final Set<T> actual, final String title) {
        final Set<T> missingExpected = new HashSet<>(expected);
        missingExpected.removeAll(actual);

        final Set<T> extraActual = new HashSet<>(actual);
        extraActual.removeAll(expected);

        assertTrue(title + ": Found missing expected " + missingExpected.toString(), missingExpected.size() == 0);
        assertTrue(title + ": Found extra actual " + extraActual.toString(), extraActual.size() == 0);
    }

    public ExternalId hasDependency(final String name, final String version, final String architecture) {
        final ExternalId id = externalIdFactory.createArchitectureExternalId(forge, name, version, architecture);
        assert graph.hasDependency(id);
        return id;
    }

    public ExternalId noDependency(final String name, final String version, final String architecture) {
        final ExternalId id = externalIdFactory.createArchitectureExternalId(forge, name, version, architecture);
        assert !graph.hasDependency(id);
        return id;
    }

    public void hasParentChildRelationship(final ExternalId parent, final ExternalId child) {
        assert graph.getChildrenExternalIdsForParent(parent).contains(child);
    }

    public void relationshipCount(final ExternalId parent, final int count) {
        assert graph.getChildrenExternalIdsForParent(parent).size() == count;
    }

    public void rootSize(final int size) {
        assert graph.getRootDependencies().size() == size;
    }

    public static void dependency(final Forge forge, final DependencyGraph graph, final String name, final String version, final String architecture) {
        new GraphAssert(forge, graph).hasDependency(name, version, architecture);
    }
}
