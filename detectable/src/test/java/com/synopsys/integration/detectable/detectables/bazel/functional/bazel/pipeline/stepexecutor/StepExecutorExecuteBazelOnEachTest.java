package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline.stepexecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutorExecuteBazelOnEach;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.BazelVariableSubstitutor;
import com.synopsys.integration.exception.IntegrationException;

public class StepExecutorExecuteBazelOnEachTest {

    @Test
    public void testNoInput() throws ExecutableRunnerException, IntegrationException {
        final File workspaceDir = new File(".");
        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final File bazelExe = new File("/usr/bin/bazel");
        final List<String> bazelArgs = new ArrayList<>();
        bazelArgs.add("query");
        bazelArgs.add("filter(\\\"@.*:jar\\\", deps(//:ProjectRunner))");
        final ExecutableOutput bazelCmdExecutableOutput = Mockito.mock(ExecutableOutput.class);
        Mockito.when(bazelCmdExecutableOutput.getReturnCode()).thenReturn(0);
        Mockito.when(bazelCmdExecutableOutput.getStandardOutput()).thenReturn("@org_apache_commons_commons_io//jar:jar\n@com_google_guava_guava//jar:jar");
        Mockito.when(executableRunner.execute(workspaceDir, bazelExe, bazelArgs)).thenReturn(bazelCmdExecutableOutput);
        final BazelCommandExecutor bazelCommandExecutor = new BazelCommandExecutor(executableRunner, workspaceDir, bazelExe);
        final BazelVariableSubstitutor bazelVariableSubstitutor = new BazelVariableSubstitutor("//:ProjectRunner", null);
        final StepExecutor executor = new StepExecutorExecuteBazelOnEach(bazelCommandExecutor, bazelVariableSubstitutor, Arrays.asList("query", "filter(\\\"@.*:jar\\\", deps(${detect.bazel.target}))"));
        final List<String> input = new ArrayList<>(0);

        final List<String> output = executor.process(input);

        assertEquals(1, output.size());
        assertEquals("@org_apache_commons_commons_io//jar:jar\n@com_google_guava_guava//jar:jar", output.get(0));
    }
}
