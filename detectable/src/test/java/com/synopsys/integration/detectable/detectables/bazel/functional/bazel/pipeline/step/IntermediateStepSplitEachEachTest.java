package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline.step;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStep;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepSplitEach;
import com.synopsys.integration.exception.IntegrationException;

public class IntermediateStepSplitEachEachTest {

    @Test
    public void test() throws IntegrationException {
        IntermediateStep intermediateStepSplitEach = new IntermediateStepSplitEach("\\s+");
        List<String> input = Arrays.asList("@org_apache_commons_commons_io//jar:jar\n@com_google_guava_guava//jar:jar");
        List<String> output = intermediateStepSplitEach.process(input);
        assertEquals(2, output.size());
        assertEquals("@org_apache_commons_commons_io//jar:jar", output.get(0));
        assertEquals("@com_google_guava_guava//jar:jar", output.get(1));
    }
}
