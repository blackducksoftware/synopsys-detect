package com.synopsys.integration.configuration.property.types.path

import java.nio.file.Path

data class PathValue(private val value: String) {
    fun resolvePath(pathResolver: PathResolver): Path {
        return pathResolver.resolvePath(value)
    }

    override fun toString(): String {
        return value
    }
}