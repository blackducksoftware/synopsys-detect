package com.synopsys.integration.configuration.property.types.path;

import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimplePathResolverTest {
    @Test
    public void resolvePathTest() {
        PathResolver simplePathResolver = new SimplePathResolver();
        Assertions.assertEquals(Paths.get("/simple/test").toAbsolutePath(), simplePathResolver.resolvePath("/simple/test"));
    }
}