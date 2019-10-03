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
package com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.detectable.detectables.bazel.model.Step;
import com.synopsys.integration.exception.IntegrationException;

public class StepExecutorExecuteBazelOnEach implements StepExecutor {
    private final BazelCommandExecutor bazelCommandExecutor;
    private final BazelVariableSubstitutor bazelVariableSubstitutor;

    public StepExecutorExecuteBazelOnEach(final BazelCommandExecutor bazelCommandExecutor, final BazelVariableSubstitutor bazelVariableSubstitutor) {
        this.bazelCommandExecutor = bazelCommandExecutor;
        this.bazelVariableSubstitutor = bazelVariableSubstitutor;
    }

    @Override
    public boolean applies(final String stepType) {
        return "executeBazelOnEach".equalsIgnoreCase(stepType);
    }

    @Override
    public List<String> process(final Step step, final List<String> input) throws IntegrationException {
        final List<String> adjustedInput;
        if (input.size() == 0) {
            adjustedInput = new ArrayList<>(1);
            adjustedInput.add(null);
        } else {
            adjustedInput = input;
        }
        final List<String> results = new ArrayList<>();
        for (final String inputItem : adjustedInput) {
            final List<String> finalizedArgs = bazelVariableSubstitutor.substitute(step.getArgs(), inputItem);
            final Optional<String> cmdOutput = bazelCommandExecutor.executeToString(finalizedArgs);
            if (cmdOutput.isPresent()) {
                results.add(cmdOutput.get());
            }
        }
        return results;
    }
}
