package com.synopsys.integration.configuration.property.types.path

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths

internal class PathValueTest {
    private class TestPathResolver : PathResolver {
        override fun resolvePath(filePath: String): Path {
            return Paths.get(filePath)
        }
    }

    @Test
    fun resolvePathTest() {
        Assertions.assertEquals(Paths.get("/test"), PathValue("/test").resolvePath(TestPathResolver()))
    }

    @Test
    fun toStringTest() {
        Assertions.assertEquals("/test/toString", PathValue("/test/toString").toString())
    }
}