/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.exception.IntegrationException;

public class IntermediateStepExecuteBazelOnEach implements IntermediateStep {
    private final BazelCommandExecutor bazelCommandExecutor;
    private final BazelVariableSubstitutor bazelVariableSubstitutor;
    private final List<String> bazelCommandArgs;

    public IntermediateStepExecuteBazelOnEach(final BazelCommandExecutor bazelCommandExecutor, final BazelVariableSubstitutor bazelVariableSubstitutor, final List<String> bazelCommandArgs) {
        this.bazelCommandExecutor = bazelCommandExecutor;
        this.bazelVariableSubstitutor = bazelVariableSubstitutor;
        this.bazelCommandArgs = bazelCommandArgs;
    }

    @Override
    public List<String> process(final List<String> input) throws IntegrationException {
        final List<String> adjustedInput;
        if (input.size() == 0) {
            adjustedInput = new ArrayList<>(1);
            adjustedInput.add(null);
        } else {
            adjustedInput = input;
        }
        final List<String> results = new ArrayList<>();
        for (final String inputItem : adjustedInput) {
            final List<String> finalizedArgs = bazelVariableSubstitutor.substitute(bazelCommandArgs, inputItem);
            final Optional<String> cmdOutput = bazelCommandExecutor.executeToString(finalizedArgs);
            if (cmdOutput.isPresent()) {
                results.add(cmdOutput.get());
            }
        }
        return results;
    }
}
