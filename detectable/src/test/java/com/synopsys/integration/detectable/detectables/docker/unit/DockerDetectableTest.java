package com.synopsys.integration.detectable.detectables.docker.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.DockerResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.JavaResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.WrongOperatingSystemResult;
import com.synopsys.integration.detectable.detectables.docker.DockerDetectable;
import com.synopsys.integration.detectable.detectables.docker.DockerDetectableOptions;
import com.synopsys.integration.detectable.detectables.docker.DockerExtractor;
import com.synopsys.integration.detectable.detectables.docker.DockerInspectorResolver;

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
