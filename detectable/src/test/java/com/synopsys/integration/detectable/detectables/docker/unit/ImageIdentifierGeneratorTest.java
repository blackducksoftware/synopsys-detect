package com.synopsys.integration.detectable.detectables.docker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.docker.ImageIdentifierGenerator;
import com.synopsys.integration.detectable.detectables.docker.ImageIdentifierType;
import com.synopsys.integration.detectable.detectables.docker.model.DockerInspectorResults;

class ImageIdentifierGeneratorTest {
    private static ImageIdentifierGenerator generator;

    @BeforeAll
    static void setup() {
        generator = new ImageIdentifierGenerator();
    }

    @Test
    void testImageName() {
        DockerInspectorResults dockerInspectorResults = new DockerInspectorResults("returnedrepo", "returnedtag", "success");
        assertEquals("suppliedrepo:suppliedtag", generator.generate(ImageIdentifierType.IMAGE_NAME, "suppliedrepo:suppliedtag", dockerInspectorResults));
    }

    @Test
    void testImageId() {
        DockerInspectorResults dockerInspectorResults = new DockerInspectorResults("returnedrepo", "returnedtag", "success");
        assertEquals("returnedrepo:returnedtag", generator.generate(ImageIdentifierType.IMAGE_ID, "suppliedrepo:suppliedtag", dockerInspectorResults));
    }

    @Test
    void testMissingReturnedRepo() {
        DockerInspectorResults dockerInspectorResults = new DockerInspectorResults(null, "returnedtag", "success");
        assertEquals("suppliedrepo:suppliedtag", generator.generate(ImageIdentifierType.IMAGE_ID, "suppliedrepo:suppliedtag", dockerInspectorResults));
    }

    @Test
    void testMissingReturnedTag() {
        DockerInspectorResults dockerInspectorResults = new DockerInspectorResults("returnedrepo", null, "success");
        assertEquals("suppliedrepo:suppliedtag", generator.generate(ImageIdentifierType.IMAGE_ID, "suppliedrepo:suppliedtag", dockerInspectorResults));
    }
}
