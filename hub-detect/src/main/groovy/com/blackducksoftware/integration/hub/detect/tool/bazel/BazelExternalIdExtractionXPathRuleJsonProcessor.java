/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.tool.bazel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

public class BazelExternalIdExtractionXPathRuleJsonProcessor {
    private final Gson gson;

    public BazelExternalIdExtractionXPathRuleJsonProcessor(final Gson gson) {
        this.gson = gson;
    }

    public List<BazelExternalIdExtractionXPathRule> load(File jsonFile) throws IOException {
        String json = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
        BazelExternalIdExtractionXPathRule[] rulesArray = gson.fromJson(json, BazelExternalIdExtractionXPathRule[].class);
        return Arrays.asList(rulesArray);
    }

    public String toJson(final List<BazelExternalIdExtractionXPathRule> rules) {
        return gson.toJson(rules);
    }
}
