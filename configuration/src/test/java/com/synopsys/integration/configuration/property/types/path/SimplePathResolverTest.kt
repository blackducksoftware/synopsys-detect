package com.synopsys.integration.configuration.property.types.path

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class SimplePathResolverTest {
    @Test
    fun resolvePathTest() {
        val simplePathResolver = SimplePathResolver()
        Assertions.assertEquals(Paths.get("/simple/test"), simplePathResolver.resolvePath("/simple/test"))
    }
}