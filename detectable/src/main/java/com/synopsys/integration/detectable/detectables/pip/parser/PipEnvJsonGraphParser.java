/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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