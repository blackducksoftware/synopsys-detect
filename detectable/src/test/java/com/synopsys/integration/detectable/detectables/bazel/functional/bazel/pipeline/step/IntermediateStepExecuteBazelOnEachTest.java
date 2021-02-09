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
package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline.step;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelVariableSubstitutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStep;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepExecuteBazelOnEach;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class IntermediateStepExecuteBazelOnEachTest {

    @Test
    public void testNoInput() throws ExecutableRunnerException, IntegrationException {
        File workspaceDir = new File(".");
        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);
        ExecutableTarget bazelExe = ExecutableTarget.forCommand("/usr/bin/bazel");
        ExecutableOutput bazelCmdExecutableOutput = Mockito.mock(ExecutableOutput.class);
        Mockito.when(bazelCmdExecutableOutput.getReturnCode()).thenReturn(0);
        Mockito.when(bazelCmdExecutableOutput.getStandardOutput()).thenReturn("@org_apache_commons_commons_io//jar:jar\n@com_google_guava_guava//jar:jar");
        Mockito.when(executableRunner.execute(Mockito.any(Executable.class))).thenReturn(bazelCmdExecutableOutput);
        BazelCommandExecutor bazelCommandExecutor = new BazelCommandExecutor(executableRunner, workspaceDir, bazelExe);
        BazelVariableSubstitutor bazelVariableSubstitutor = new BazelVariableSubstitutor("//:ProjectRunner", null);
        IntermediateStep executor = new IntermediateStepExecuteBazelOnEach(bazelCommandExecutor, bazelVariableSubstitutor, Arrays.asList("cquery", "filter(\\\"@.*:jar\\\", deps(${detect.bazel.target}))"), false);
        List<String> input = new ArrayList<>(0);

        List<String> output = executor.process(input);

        assertEquals(1, output.size());
        assertEquals("@org_apache_commons_commons_io//jar:jar\n@com_google_guava_guava//jar:jar", output.get(0));
    }
}
