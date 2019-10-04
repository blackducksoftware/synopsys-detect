/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.bazel.pipeline;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.bazel.model.Step;
import com.synopsys.integration.exception.IntegrationException;

public class BazelPipelineJsonProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Gson gson;

    public BazelPipelineJsonProcessor(final Gson gson) {
        this.gson = gson;
    }

    @NotNull
    public List<Step> fromJsonString(final String json) throws IntegrationException {
        final Step[] pipelineSteps = gson.fromJson(json, Step[].class);
        if ((pipelineSteps == null) || (pipelineSteps.length == 0)) {
            logger.debug(String.format("No pipeline steps found in:\n%s", json));
            throw new IntegrationException("No pipeline steps found");
        }
        return Arrays.asList(pipelineSteps);
    }
}
