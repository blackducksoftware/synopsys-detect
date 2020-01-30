package com.synopsys.integration.configuration.property.types.path

import java.nio.file.Path
import java.nio.file.Paths

class SimplePathResolver : PathResolver {
    override fun resolvePath(filePath: String): Path {
        return Paths.get(filePath)
    }
}