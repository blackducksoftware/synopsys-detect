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

import static com.synopsys.integration.detect.battery.BatteryFiles.UTIL_RESOURCE_PREFIX;

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
import org.codehaus.plexus.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.skyscreamer.jsonassert.JSONParser;

public class BdioCompareTests {
    @Test
   public void actualMissingTwoNodes() throws IOException, JSONException, BdioCompare.BdioCompareException {
        //two-root has two nodes from the root, so when compared to empty bdio we should get 2 differences of the 2 missing nodes.
       JSONArray expected = loadBdio("two-root-bdio");
       JSONArray actual = loadBdio("empty-bdio");
       Assertions.assertEquals(2, new BdioCompare().compare(expected, actual).size());
   }

    @Test
    public void actualMissingRelationship() throws IOException, JSONException, BdioCompare.BdioCompareException {
        //two-root-related has a relationship between node1 and node2 that two-root does not, so we should get a difference of 1.
        JSONArray expected = loadBdio("two-root-related-bdio");
        JSONArray actual = loadBdio("two-root-bdio");
        Assertions.assertEquals(1, new BdioCompare().compare(expected, actual).size());
    }

    @Test
    public void identicalMatches() throws IOException, JSONException, BdioCompare.BdioCompareException {
        JSONArray expected = loadBdio("two-root-bdio");
        JSONArray actual = loadBdio("two-root-bdio");
        Assertions.assertEquals(0, new BdioCompare().compare(expected, actual).size());
    }

   private JSONArray loadBdio(String name) throws JSONException, IOException {
       File file = BatteryFiles.asFile("/bdio-compare/" + name + ".jsonld", UTIL_RESOURCE_PREFIX);
       final String text = FileUtils.readFileToString(file, Charset.defaultCharset());
       return (JSONArray) JSONParser.parseJSON(text);
   }
}


