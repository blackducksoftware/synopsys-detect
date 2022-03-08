package com.synopsys.integration.configuration.property.types.path;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PathValueTest {
    private static class TestPathResolver implements PathResolver {
        @Override
        public Path resolvePath(String filePath) {
            return Paths.get(filePath);
        }
    }

    @Test
    public void resolvePathTest() {
        Assertions.assertEquals(Paths.get("/test"), new PathValue("/test").resolvePath(new TestPathResolver()));
    }

    @Test
    public void toStringTest() {
        Assertions.assertEquals("/test/toString", new PathValue("/test/toString").toString());
    }
}