package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.model.Step;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.StepExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.StepExecutorFilter;
import com.synopsys.integration.exception.IntegrationException;

public class StepExecutorFilterTest {

    private static final String NAME_LINE = "  name = \"com_google_code_findbugs_jsr305\",";
    private static final String TAGS_LINE = "  tags = [\"maven_coordinates=com.google.code.findbugs:jsr305:3.0.2\"],";

    @Test
    public void test() throws IntegrationException {
        final StepExecutor stepExecutor = new StepExecutorFilter();
        assertTrue(stepExecutor.applies("filter"));
        final Step step = new Step("filter", Arrays.asList(".*maven_coordinates=.*"));

        final List<String> input = Arrays.asList(NAME_LINE, TAGS_LINE);
        final List<String> output = stepExecutor.process(step, input);
        assertEquals(1, output.size());
        assertEquals(TAGS_LINE, output.get(0));
    }
}
