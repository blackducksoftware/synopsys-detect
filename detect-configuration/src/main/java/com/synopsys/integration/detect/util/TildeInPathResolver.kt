/**
 * detect-configuration
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.util

import com.synopsys.integration.configuration.property.types.path.PathResolver
import org.slf4j.LoggerFactory
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths

/**
 * // @formatter:off
 * Users can pass paths to detect that begin with a '~' character. In linux/mac environments, this
 * is shorthand for the user's home directory. If we encounter a property that is formed this way,
 * we can resolve it.
 *
 * If there is concern that this will be too invasive, users can specify
 * --detect.resolve.tilde.in.paths=false to turn it off.
 * // @formatter:on
 */
class TildeInPathResolver(private val systemUserHome: String) : PathResolver {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Throws(InvalidPathException::class)
    override fun resolvePath(filePath: String): Path {
        val resolved = if (filePath.startsWith("~/")) {
            systemUserHome + filePath.substring(1)
        } else {
            filePath
        }
        if (resolved != filePath) {
            logger.trace(String.format("We have resolved %s to %s. If this is not expected, please revise the path provided, or specify --detect.resolve.tilde.in.paths=false.", filePath, resolved))
        }
        return Paths.get(resolved)
    }
}