/**
 * synopsys-detect
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
package com.synopsys.integration.detect.battery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

public class BdioCompare {
    private class BdioDocument {
        public List<BdioNode> nodes = new ArrayList<>();
    }

    private static class BdioNode {
        public String name = "";
        public String revision = "";
        public String id = "";
        public List<String> relatedIds = new ArrayList<>();

        public BdioNode(String name, String revision, String id, List<String> relatedIds) {
            this.name = name;
            this.revision = revision;
            this.id = id;
            this.relatedIds = relatedIds;
        }

        public String toDescription() {
            if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(revision)) {
                return "(" + name + ", " + revision + ")";
            } else if (StringUtils.isNotBlank(name)) {
                return "(" + name + ")";
            } else {
                return "(" + id + ")";
            }
        }
    }

    public static class BdioIssue {
        private final String issue;

        private BdioIssue(String issue) {
            this.issue = issue;
        }

        public String getIssue() {
            return issue;
        }
    }

    public static class BdioCompareException extends Exception {
        public BdioCompareException(String message) {
            super(message);
        }
    }

    List<BdioIssue> compare(JSONArray expectedJson, JSONArray actualJson) throws BdioCompareException {
        List<BdioIssue> issues = new ArrayList<>();
        BdioDocument expected = parse(expectedJson);
        BdioDocument actual = parse(actualJson);

        List<BdioNode> missingExpected = new ArrayList<>();
        Set<String> actualIds = actual.nodes.stream().skip(1).map(bdioNode -> bdioNode.id).collect(Collectors.toSet());
        Set<String> expectedIds = expected.nodes.stream().skip(1).map(bdioNode -> bdioNode.id).collect(Collectors.toSet());
        Set<String> differentIds = SetUtils.disjunction(actualIds, expectedIds);

        for (String differentId : differentIds) {
            Optional<BdioNode> expectedNode = expected.nodes.stream().filter(it -> it.id.equals(differentId)).findFirst();
            if (expectedNode.isPresent()) {
                issues.add(new BdioIssue("An expected component was not found in the bdio with id " + expectedNode.get().toDescription() + "."));
            } else {
                Optional<BdioNode> actualNode = actual.nodes.stream().filter(it -> it.id.equals(differentId)).findFirst();
                if (actualNode.isPresent()) {
                    issues.add(new BdioIssue("There was an additional component in the created bdio node " + actualNode.get().toDescription() + "."));
                } else {
                    throw new BdioCompareException("Something went wrong comparing BDIO. An id was different in " +
                            "actual and in expected but neither actually had the node.");
                }
            }
        }

        //For each expected, verify the number of relationships.
        boolean first = true;
        for (BdioNode expectedNode : expected.nodes) {
            if (first) {
                first = false;
                continue;
            }
            for (BdioNode actualNode : actual.nodes) {
                if (expectedNode.id.equals(actualNode.id)) {
                    Set<String> differenceRelated = SetUtils.disjunction(new HashSet<>(expectedNode.relatedIds), new HashSet<>(actualNode.relatedIds));
                    for (String difference : differenceRelated) {
                        if (expectedNode.relatedIds.contains(difference)) {
                            issues.add(new BdioIssue(
                                    "There was a missing relationship (in expected but not actual) in the component " + expectedNode.toDescription() + " to the component " + firstComponent(difference, expected, actual).toDescription() + "."));
                        } else if (actualNode.relatedIds.contains(difference)) {
                            issues.add(new BdioIssue(
                                    "There was an additional relationship (in actual but not in expected) in the " +
                                            "component " + expectedNode.toDescription() + " to the component " + firstComponent(difference, expected, actual).toDescription()
                                            + "."));
                        } else {
                            throw new BdioCompareException("Something went wrong comparing BDIO. An related id was " +
                                    "different in actual and in expected BDIO nodes, but neither actually had the relationship.");
                        }
                    }
                }
            }
        }
        return issues;
    }

    BdioNode firstComponent(String id, BdioDocument expected, BdioDocument actual) throws BdioCompareException {
        Optional<BdioNode> expectedNode = expected.nodes.stream().filter(it -> it.id.equals(id)).findFirst();
        if (expectedNode.isPresent()) {
            return expectedNode.get();
        }
        Optional<BdioNode> actualNode = actual.nodes.stream().filter(it -> it.id.equals(id)).findFirst();
        if (actualNode.isPresent()) {
            return actualNode.get();
        }
        throw new BdioCompareException("Something went wrong comparing BDIO. Could not find component id in actual or expected: " + actual);
    }

    BdioDocument parse(JSONArray json) { // TODO: Don't even both with the first node.
        BdioDocument document = new BdioDocument();
        for (int i = 0; i < json.length(); i++) {
            try {
                JSONObject component = json.getJSONObject(i);
                String name = component.optString("name");
                String revision = component.optString("revision");
                String id = "";
                if (i != 0) { //Only allow the first node (@BillOfMaterials) to have an empty or missing @id.
                    id = component.getString("@id");
                    Assertions.assertTrue(StringUtils.isNotBlank(id));
                }
                List<String> related = new ArrayList<>();
                JSONArray relatedJson = component.getJSONArray("relationship");
                for (int r = 0; r < relatedJson.length(); r++) {
                    JSONObject relatedObj = relatedJson.getJSONObject(r);
                    String relatedId = relatedObj.getString("related");
                    related.add(relatedId);
                }
                document.nodes.add(new BdioNode(name, revision, id, related));
            } catch (JSONException e) {
                Assertions.assertNull(e, "When converting bdio into Java Objects, an error occurred comparing the bdio objects.");
            }
        }
        return document;
    }
}


