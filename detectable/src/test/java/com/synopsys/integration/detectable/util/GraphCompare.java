/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.util;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.BdioId;

public class GraphCompare {
    public static void assertEqualsResource(final String expectedResourceFile, final DependencyGraph actualGraph) {
        final DependencyGraphSummarizer summarizer = new DependencyGraphSummarizer(new Gson());

        final String json = FunctionalTestFiles.asString(expectedResourceFile);

        final GraphSummary expected = summarizer.fromJson(json);
        final GraphSummary actual = summarizer.fromGraph(actualGraph);
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(actual));
        assertSummaries(expected, actual);
    }

    public static void assertEquals(final DependencyGraph expectedGraph, final DependencyGraph actualGraph) {
        final DependencyGraphSummarizer summarizer = new DependencyGraphSummarizer(new Gson());
        final GraphSummary expected = summarizer.fromGraph(expectedGraph);
        final GraphSummary actual = summarizer.fromGraph(actualGraph);
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(actual));
        assertSummaries(expected, actual);
    }

    private static void assertSummaries(final GraphSummary expected, final GraphSummary actual) {
        assertSet(expected.rootExternalDataIds, actual.rootExternalDataIds, "Root external ids");
        assertSet(expected.dependencySummaries.keySet(), actual.dependencySummaries.keySet(), "Dependencies in graph");

        final Set<BdioId> expectedRelationshipIds = expected.externalDataIdRelationships.keySet();
        final Set<BdioId> expectedExistingRelationshipsIds = expectedRelationshipIds.stream().filter(key -> expected.externalDataIdRelationships.get(key) != null && expected.externalDataIdRelationships.get(key).size() > 0)
                                                                 .collect(Collectors.toSet());

        final Set<BdioId> actualRelationshipIds = actual.externalDataIdRelationships.keySet();
        final Set<BdioId> actualExistingRelationshipsIds = actualRelationshipIds.stream().filter(key -> actual.externalDataIdRelationships.get(key) != null && actual.externalDataIdRelationships.get(key).size() > 0)
                                                               .collect(Collectors.toSet());

        assertSet(expectedExistingRelationshipsIds, actualExistingRelationshipsIds, "Existing relationships");

        for (final BdioId key : expected.dependencySummaries.keySet()) {
            Assertions.assertEquals(expected.dependencySummaries.get(key).getName(), actual.dependencySummaries.get(key).getName());
            Assertions.assertEquals(expected.dependencySummaries.get(key).getVersion(), actual.dependencySummaries.get(key).getVersion());
        }
        for (final BdioId key : expectedExistingRelationshipsIds) {
            assertSet(expected.externalDataIdRelationships.get(key), actual.externalDataIdRelationships.get(key), "External data id relationships for " + key);
        }
    }

    private static <T> void assertSet(final Set<T> expected, final Set<T> actual, final String title) {
        final Set<T> missingExpected = new HashSet<>(expected);
        missingExpected.removeAll(actual);

        final Set<T> extraActual = new HashSet<>(actual);
        extraActual.removeAll(expected);

        Assertions.assertEquals(0, missingExpected.size(), title + ": Missing expected " + missingExpected.toString());
        Assertions.assertEquals(0, extraActual.size(), title + ": Found extra " + extraActual.toString());
    }
}
