/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.swift;

import java.util.List;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.swift.model.SwiftPackage;

public class SwiftCliParser {
    private final Gson gson;

    public SwiftCliParser(final Gson gson) {
        this.gson = gson;
    }

    public SwiftPackage parseOutput(final List<String> lines) {
        boolean started = false;
        final StringBuilder jsonStringBuilder = new StringBuilder();
        for (final String line : lines) {
            if (!started && line.startsWith("{")) {
                started = true;
            } else if (!started) {
                continue;
            }

            jsonStringBuilder.append(line);
            jsonStringBuilder.append(System.lineSeparator());

            if (line.startsWith("}")) {
                break;
            }
        }
        final String jsonText = jsonStringBuilder.toString();

        return gson.fromJson(jsonText, SwiftPackage.class);
    }
}
