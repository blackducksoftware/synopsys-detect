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
package com.synopsys.integration.detectable.detectables.pip.parser;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraph;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraphEntry;

public class PipEnvJsonGraphParser {
    private static final Type PIPENV_GRAPH_ENTRY_TYPE = new TypeToken<List<PipenvGraphEntry>>() {}.getType();

    @NotNull
    private final Gson gson;

    public PipEnvJsonGraphParser(@NotNull final Gson gson) {
        this.gson = gson;
    }

    @NotNull
    public PipenvGraph parse(@NotNull final String pipEnvGraphOutput) {
        final List<PipenvGraphEntry> entries = gson.fromJson(pipEnvGraphOutput, PIPENV_GRAPH_ENTRY_TYPE);
        return new PipenvGraph(entries.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }
}