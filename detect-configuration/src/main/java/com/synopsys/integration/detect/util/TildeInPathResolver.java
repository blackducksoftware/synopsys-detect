/**
 * detect-configuration
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.util;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.type.OperatingSystemType;

/**
 * // @formatter:off
 * Users can pass paths to detect that begin with a '~' character. In linux/mac environments, this
 * is shorthand for the user's home directory. If we encounter a property that is formed this way,
 * we can resolve it.
 *
 * To accomplish this, we will look at all fields annotated with @ValueDescripition in
 * DetectConfiguration and if we find *any* property that starts with '~/' we will replace it.
 *
 * If there is concern that this will be too invasive, users can specify
 * --detect.resolve.tilde.in.paths=false to turn it off.
 * // @formatter:on
 */
public class TildeInPathResolver {
    private final Logger logger = LoggerFactory.getLogger(TildeInPathResolver.class);

    private final String systemUserHome;
    private final OperatingSystemType currentOs;

    public TildeInPathResolver(final String systemUserHome, final OperatingSystemType currentOs) {
        this.systemUserHome = systemUserHome;
        this.currentOs = currentOs;
    }

    public Optional<String> resolveTildeInValue(final String value) {
        final String originalString = value;
        if (StringUtils.isNotBlank(originalString)) {
            final String resolvedPath = resolveTildeInPath(currentOs, systemUserHome, originalString);
            if (!resolvedPath.equals(originalString)) {
                logger.warn(String.format("We have resolved %s to %s. If this is not expected, please revise the path provided, or specify --detect.resolve.tilde.in.paths=false.", originalString, resolvedPath));
                return Optional.of(resolvedPath);
            }
        }
        return Optional.empty();
    }

    public String resolveTildeInPath(final OperatingSystemType currentOs, final String systemUserHome, final String filePath) {
        if (OperatingSystemType.WINDOWS == currentOs || StringUtils.isBlank(filePath)) {
            return filePath;
        }

        if (filePath.startsWith("~/")) {
            return systemUserHome + filePath.substring(1);
        }

        return filePath;
    }

}
