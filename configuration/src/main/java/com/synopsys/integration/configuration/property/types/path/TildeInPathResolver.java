/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.configuration.property.types.path;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TildeInPathResolver implements PathResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String systemUserHome;

    public TildeInPathResolver(final String systemUserHome) {
        this.systemUserHome = systemUserHome;
    }

    /**
     * Resolves a '~' character at the start of [filePath]. In linux/mac environments, this
     * is shorthand for the user's home directory. If we encounter a property that
     * is formed this way, we can resolve it.
     */
    @Override
    public Path resolvePath(final String filePath) {
        final String resolved;
        if (filePath.startsWith("~/")) {
            resolved = systemUserHome + filePath.substring(1);
        } else {
            resolved = filePath;
        }

        if (!resolved.equals(filePath)) {
            // TODO: Add callback for this? Properties should not be explicitly referenced in the configuration module.
            logger.trace(String.format("We have resolved %s to %s. If this is not expected, please revise the path provided, or specify --detect.resolve.tilde.in.paths=false.", filePath, resolved));
        }

        return Paths.get(resolved);
    }
}

