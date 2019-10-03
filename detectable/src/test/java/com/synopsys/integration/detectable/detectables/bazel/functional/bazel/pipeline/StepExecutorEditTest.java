package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.model.Step;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.StepExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.StepExecutorEdit;
import com.synopsys.integration.exception.IntegrationException;

public class StepExecutorEditTest {

    @Test
    public void testRemoveLeadingAtSign() throws IntegrationException {
        final StepExecutor stepExecutor = new StepExecutorEdit();
        assertTrue(stepExecutor.applies("edit"));
        final Step step = new Step("edit", Arrays.asList("^@", ""));
        final List<String> input = Arrays.asList("@org_apache_commons_commons_io//jar:jar", "@com_google_guava_guava//jar:jar");
        final List<String> output = stepExecutor.process(step, input);
        assertEquals(2, output.size());
        assertEquals("org_apache_commons_commons_io//jar:jar", output.get(0));
        assertEquals("com_google_guava_guava//jar:jar", output.get(1));
    }

    @Test
    public void testRemoveTrailingJunk() throws IntegrationException {
        final StepExecutor stepExecutor = new StepExecutorEdit();
        assertTrue(stepExecutor.applies("edit"));
        final Step step = new Step("edit", Arrays.asList("//.*", ""));
        final List<String> input = Arrays.asList("org_apache_commons_commons_io//jar:jar", "com_google_guava_guava//jar:jar");
        final List<String> output = stepExecutor.process(step, input);
        assertEquals(2, output.size());
        assertEquals("org_apache_commons_commons_io", output.get(0));
        assertEquals("com_google_guava_guava", output.get(1));
    }

    @Test
    public void testInsertPrefix() throws IntegrationException {
        final StepExecutor stepExecutor = new StepExecutorEdit();
        assertTrue(stepExecutor.applies("edit"));
        final Step step = new Step("edit", Arrays.asList("^", "//external:"));
        final List<String> input = Arrays.asList("org_apache_commons_commons_io", "com_google_guava_guava");
        final List<String> output = stepExecutor.process(step, input);
        assertEquals(2, output.size());
        assertEquals("//external:org_apache_commons_commons_io", output.get(0));
        assertEquals("//external:com_google_guava_guava", output.get(1));
    }

    @Test
    public void testMavenInstallBuildOutputExtractMavenCoordinates() throws IntegrationException {
        final List<String> input = Arrays.asList("  tags = [\"maven_coordinates=com.google.guava:guava:27.0-jre\"],");
        final StepExecutor stepExecutor = new StepExecutorEdit();
        assertTrue(stepExecutor.applies("edit"));

        final Step stepOne = new Step("edit", Arrays.asList("^\\s*tags\\s*\\s*=\\s*\\[\\s*\"maven_coordinates=", ""));
        final List<String> stepOneOutput = stepExecutor.process(stepOne, input);

        final Step stepTwo = new Step("edit", Arrays.asList("\".*", ""));
        final List<String> output = stepExecutor.process(stepTwo, stepOneOutput);

        assertEquals(1, output.size());
        assertEquals("com.google.guava:guava:27.0-jre", output.get(0));
    }
}
