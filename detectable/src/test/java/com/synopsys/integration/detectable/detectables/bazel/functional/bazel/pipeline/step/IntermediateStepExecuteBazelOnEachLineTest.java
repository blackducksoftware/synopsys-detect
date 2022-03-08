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
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelVariableSubstitutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStep;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepExecuteBazelOnEachLine;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class IntermediateStepExecuteBazelOnEachLineTest {

    @Test
    public void testNoInput() throws ExecutableRunnerException, IntegrationException, ExecutableFailedException {
        File workspaceDir = new File(".");
        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);
        ExecutableTarget bazelExe = ExecutableTarget.forCommand("/usr/bin/bazel");
        ExecutableOutput bazelCmdExecutableOutput = Mockito.mock(ExecutableOutput.class);
        Mockito.when(bazelCmdExecutableOutput.getReturnCode()).thenReturn(0);
        Mockito.when(bazelCmdExecutableOutput.getStandardOutput()).thenReturn("@org_apache_commons_commons_io//jar:jar\n@com_google_guava_guava//jar:jar");
        Mockito.when(executableRunner.executeSuccessfully(Mockito.any(Executable.class))).thenReturn(bazelCmdExecutableOutput);
        BazelCommandExecutor bazelCommandExecutor = new BazelCommandExecutor(executableRunner, workspaceDir, bazelExe);
        BazelVariableSubstitutor bazelVariableSubstitutor = new BazelVariableSubstitutor("//:ProjectRunner", null);
        IntermediateStep executor = new IntermediateStepExecuteBazelOnEachLine(
            bazelCommandExecutor,
            bazelVariableSubstitutor,
            Arrays.asList("cquery", "filter(\\\"@.*:jar\\\", deps(${detect.bazel.target}))"),
            false
        );
        List<String> input = new ArrayList<>(0);

        List<String> output = executor.process(input);

        assertEquals(1, output.size());
        assertEquals("@org_apache_commons_commons_io//jar:jar\n@com_google_guava_guava//jar:jar", output.get(0));
    }
}
