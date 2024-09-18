package com.blackduck.integration.detectable.detectables.pipenv.buildexe.parser;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.blackduck.integration.detectable.detectables.pipenv.buildexe.model.PipenvGraphEntry;
import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.blackduck.integration.detectable.detectables.pipenv.buildexe.model.PipenvGraph;

public class PipEnvJsonGraphParser {
    private static final Type PIPENV_GRAPH_ENTRY_TYPE = new TypeToken<List<PipenvGraphEntry>>() {}.getType();

    @NotNull
    private final Gson gson;

    public PipEnvJsonGraphParser(@NotNull Gson gson) {
        this.gson = gson;
    }

    @NotNull
    public PipenvGraph parse(@NotNull String pipEnvGraphOutput) {
        List<PipenvGraphEntry> entries = gson.fromJson(pipEnvGraphOutput, PIPENV_GRAPH_ENTRY_TYPE);
        return new PipenvGraph(entries.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }
}