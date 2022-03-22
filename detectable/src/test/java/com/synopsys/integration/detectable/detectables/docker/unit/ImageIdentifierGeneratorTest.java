package com.synopsys.integration.detectable.detectables.docker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.docker.ImageIdentifierGenerator;
import com.synopsys.integration.detectable.detectables.docker.ImageIdentifierType;
import com.synopsys.integration.detectable.detectables.docker.model.DockerImageInfo;

public class ImageIdentifierGeneratorTest {
    private static ImageIdentifierGenerator generator;

    @BeforeAll
    static void setup() {
        generator = new ImageIdentifierGenerator();
    }

    @Test
    void testImageName() {
        DockerImageInfo dockerImageInfo = new DockerImageInfo("returnedrepo", "returnedtag", "success");
        assertEquals("suppliedrepo:suppliedtag", generator.deriveImageIdentifier(ImageIdentifierType.IMAGE_NAME, "suppliedrepo:suppliedtag", dockerImageInfo));
    }

    @Test
    void testImageId() {
        DockerImageInfo dockerImageInfo = new DockerImageInfo("returnedrepo", "returnedtag", "success");
        assertEquals("returnedrepo:returnedtag", generator.deriveImageIdentifier(ImageIdentifierType.IMAGE_ID, "suppliedrepo:suppliedtag", dockerImageInfo));
    }

    @Test
    void testMissingReturnedRepo() {
        DockerImageInfo dockerImageInfo = new DockerImageInfo(null, "returnedtag", "success");
        assertEquals("suppliedrepo:suppliedtag", generator.deriveImageIdentifier(ImageIdentifierType.IMAGE_ID, "suppliedrepo:suppliedtag", dockerImageInfo));
    }

    @Test
    void testMissingReturnedTag() {
        DockerImageInfo dockerImageInfo = new DockerImageInfo("returnedrepo", null, "success");
        assertEquals("suppliedrepo:suppliedtag", generator.deriveImageIdentifier(ImageIdentifierType.IMAGE_ID, "suppliedrepo:suppliedtag", dockerImageInfo));
    }
}
