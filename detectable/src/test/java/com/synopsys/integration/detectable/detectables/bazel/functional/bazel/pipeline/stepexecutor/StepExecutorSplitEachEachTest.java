package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline.stepexecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutorSplitEach;
import com.synopsys.integration.exception.IntegrationException;

public class StepExecutorSplitEachEachTest {

    @Test
    public void test() throws IntegrationException {
        final StepExecutor stepExecutorSplitEach = new StepExecutorSplitEach("\\s+");
        final List<String> input = Arrays.asList("@org_apache_commons_commons_io//jar:jar\n@com_google_guava_guava//jar:jar");
        final List<String> output = stepExecutorSplitEach.process(input);
        assertEquals(2, output.size());
        assertEquals("@org_apache_commons_commons_io//jar:jar", output.get(0));
        assertEquals("@com_google_guava_guava//jar:jar", output.get(1));
    }
}
