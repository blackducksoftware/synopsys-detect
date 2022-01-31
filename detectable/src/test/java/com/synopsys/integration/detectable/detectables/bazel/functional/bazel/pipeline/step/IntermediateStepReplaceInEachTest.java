package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline.step;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStep;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepReplaceInEach;
import com.synopsys.integration.exception.IntegrationException;

public class IntermediateStepReplaceInEachTest {

    @Test
    public void testRemoveLeadingAtSign() throws IntegrationException {
        List<String> input = Arrays.asList("@org_apache_commons_commons_io//jar:jar", "@com_google_guava_guava//jar:jar");
        IntermediateStep intermediateStep = new IntermediateStepReplaceInEach("^@", "");
        List<String> output = intermediateStep.process(input);
        assertEquals(2, output.size());
        assertEquals("org_apache_commons_commons_io//jar:jar", output.get(0));
        assertEquals("com_google_guava_guava//jar:jar", output.get(1));
    }

    @Test
    public void testRemoveTrailingJunk() throws IntegrationException {
        List<String> input = Arrays.asList("org_apache_commons_commons_io//jar:jar", "com_google_guava_guava//jar:jar");
        IntermediateStep intermediateStep = new IntermediateStepReplaceInEach("//.*", "");
        List<String> output = intermediateStep.process(input);
        assertEquals(2, output.size());
        assertEquals("org_apache_commons_commons_io", output.get(0));
        assertEquals("com_google_guava_guava", output.get(1));
    }

    @Test
    public void testInsertPrefix() throws IntegrationException {
        List<String> input = Arrays.asList("org_apache_commons_commons_io", "com_google_guava_guava");
        IntermediateStep intermediateStep = new IntermediateStepReplaceInEach("^", "//external:");
        List<String> output = intermediateStep.process(input);
        assertEquals(2, output.size());
        assertEquals("//external:org_apache_commons_commons_io", output.get(0));
        assertEquals("//external:com_google_guava_guava", output.get(1));
    }

    @Test
    public void testMavenInstallBuildOutputExtractMavenCoordinates() throws IntegrationException {
        List<String> input = Arrays.asList("  tags = [\"maven_coordinates=com.google.guava:guava:27.0-jre\"],");
        IntermediateStep intermediateStepOne = new IntermediateStepReplaceInEach("^\\s*tags\\s*\\s*=\\s*\\[\\s*\"maven_coordinates=", "");
        List<String> stepOneOutput = intermediateStepOne.process(input);

        IntermediateStep intermediateStepTwo = new IntermediateStepReplaceInEach("\".*", "");
        List<String> output = intermediateStepTwo.process(stepOneOutput);

        assertEquals(1, output.size());
        assertEquals("com.google.guava:guava:27.0-jre", output.get(0));
    }

    @Test
    public void testRemoveLeadingAtSignMixedTags() throws IntegrationException {
        List<String> input = Arrays.asList("  tags = [\"__SOME_OTHER_TAG__\", \"maven_coordinates=com.company.thing:thing-common-client:2.100.0\"],", "  tags = [\"maven_coordinates=com.google.code.findbugs:jsr305:3.0.2\"],");
        IntermediateStep intermediateStep1 = new IntermediateStepReplaceInEach(".*\"maven_coordinates=", "");
        IntermediateStep intermediateStep2 = new IntermediateStepReplaceInEach("\".*", "");
        List<String> intermediate = intermediateStep1.process(input);
        List<String> output = intermediateStep2.process(intermediate);

        assertEquals(2, output.size());
        assertEquals("com.company.thing:thing-common-client:2.100.0", output.get(0));
        assertEquals("com.google.code.findbugs:jsr305:3.0.2", output.get(1));
    }
}
