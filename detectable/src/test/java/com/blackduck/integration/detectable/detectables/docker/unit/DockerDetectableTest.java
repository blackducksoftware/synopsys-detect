package com.blackduck.integration.detectable.detectables.docker.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.detectable.executable.resolver.DockerResolver;
import com.blackduck.integration.detectable.detectable.executable.resolver.JavaResolver;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.detectable.result.WrongOperatingSystemResult;
import com.blackduck.integration.detectable.detectables.docker.DockerDetectable;
import com.blackduck.integration.detectable.detectables.docker.DockerDetectableOptions;
import com.blackduck.integration.detectable.detectables.docker.DockerExtractor;
import com.blackduck.integration.detectable.detectables.docker.DockerInspectorResolver;

public class DockerDetectableTest {

    @Test
    public void testApplicable() {
        DetectableEnvironment environment = null;
        DockerInspectorResolver dockerInspectorResolver = null;
        JavaResolver javaResolver = null;
        DockerResolver dockerResolver = null;
        DockerExtractor dockerExtractor = null;
        DockerDetectableOptions dockerDetectableOptions = Mockito.mock(DockerDetectableOptions.class);

        Mockito.when(dockerDetectableOptions.hasDockerImageOrTar()).thenReturn(Boolean.TRUE);

        DockerDetectable detectable = new DockerDetectable(environment, dockerInspectorResolver, javaResolver, dockerResolver, dockerExtractor, dockerDetectableOptions);

        DetectableResult result = detectable.applicable();

        assertTrue(result.getPassed() || result instanceof WrongOperatingSystemResult);
    }
}
