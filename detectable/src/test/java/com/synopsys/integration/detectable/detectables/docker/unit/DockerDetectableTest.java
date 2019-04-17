package com.synopsys.integration.detectable.detectables.docker.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.BashResolver;
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

        final DetectableEnvironment environment = null;
        final DockerInspectorResolver dockerInspectorResolver = null;
        final JavaResolver javaResolver = null;
        final BashResolver bashResolver = null;
        final DockerResolver dockerResolver = null;
        final DockerExtractor dockerExtractor = null;
        final DockerDetectableOptions dockerDetectableOptions = Mockito.mock(DockerDetectableOptions.class);

        Mockito.when(dockerDetectableOptions.hasDockerImageOrTag()).thenReturn(Boolean.TRUE);

        final DockerDetectable detectable = new DockerDetectable(environment, dockerInspectorResolver, javaResolver, bashResolver, dockerResolver,
         dockerExtractor, dockerDetectableOptions);

        final DetectableResult result = detectable.applicable();

        assertTrue(result.getPassed() || result instanceof WrongOperatingSystemResult);
    }
}
