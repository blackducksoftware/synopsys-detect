package com.synopsys.integration.detectable.detectables.bazel.model.pipeline;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.bazel.model.BazelExternalIdExtractionFullRule;

public class PipelineJsonProcessor {
    private final Gson gson;

    public PipelineJsonProcessor(final Gson gson) {
        this.gson = gson;
    }

    public List<Step> load(File jsonFile) throws IOException {
        String json = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
        final Step[] pipelineSteps = gson.fromJson(json, Step[].class);
        return Arrays.asList(pipelineSteps);
    }

    public String toJson(final List<Step> pipelineSteps) {
        return gson.toJson(pipelineSteps);
    }
}
