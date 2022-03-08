package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline.step;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStep;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepParseFilterLines;
import com.synopsys.integration.exception.IntegrationException;

public class IntermediateStepParseFilterLinesTest {

    private static final String NAME_LINE = "  name = \"com_google_code_findbugs_jsr305\",";
    private static final String TAGS_LINE_MAVEN_COORDINATES = "  tags = [\"maven_coordinates=com.google.code.findbugs:jsr305:3.0.2\"],";
    private static final String TAGS_LINE_OTHER = "  tags = [\"__SOME_OTHER_TAG__\"],";
    private static final String TAGS_LINE_MIXED = "  tags = [\"__SOME_OTHER_TAG__\", \"maven_coordinates=com.company.thing:thing-common-client:2.100.0\"],";

    @Test
    public void testMavenCoordinateOnly() throws IntegrationException, ExecutableFailedException {
        IntermediateStep intermediateStep = new IntermediateStepParseFilterLines(".*maven_coordinates=.*");

        List<String> input = Arrays.asList(NAME_LINE, TAGS_LINE_MAVEN_COORDINATES);
        List<String> output = intermediateStep.process(input);
        assertEquals(1, output.size());
        assertEquals(TAGS_LINE_MAVEN_COORDINATES, output.get(0));
    }

    @Test
    public void testOtherTagType() throws IntegrationException, ExecutableFailedException {
        IntermediateStep intermediateStep = new IntermediateStepParseFilterLines(".*maven_coordinates=.*");

        List<String> input = Arrays.asList(NAME_LINE, TAGS_LINE_OTHER);
        List<String> output = intermediateStep.process(input);
        assertEquals(0, output.size());
    }

    @Test
    public void testMixed() throws IntegrationException, ExecutableFailedException {
        IntermediateStep intermediateStep = new IntermediateStepParseFilterLines(".*maven_coordinates=.*");

        List<String> input = Arrays.asList(NAME_LINE, TAGS_LINE_MIXED);
        List<String> output = intermediateStep.process(input);
        assertEquals(1, output.size());
        assertEquals(TAGS_LINE_MIXED, output.get(0));
    }
}
