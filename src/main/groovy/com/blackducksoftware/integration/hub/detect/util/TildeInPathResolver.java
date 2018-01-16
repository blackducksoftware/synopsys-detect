/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.util;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.help.ValueDescription;
import com.blackducksoftware.integration.hub.detect.type.OperatingSystemType;

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
@Component
public class TildeInPathResolver {
    private final Logger logger = LoggerFactory.getLogger(TildeInPathResolver.class);

    @Autowired
    private DetectInfo detectInfo;

    public void resolveTildeInAllPathFields(final String systemUserHome, final DetectConfiguration detectConfiguration) throws IllegalArgumentException, IllegalAccessException {
        final OperatingSystemType currentOs = detectInfo.getCurrentOs();
        final Field[] fields = DetectConfiguration.class.getDeclaredFields();
        for (final Field field : fields) {
            if (field.isAnnotationPresent(ValueDescription.class) && field.getType() == String.class) {
                final boolean wasAccessible = field.isAccessible();
                field.setAccessible(true);
                final String providedPath = (String) field.get(detectConfiguration);
                if (StringUtils.isNotBlank(providedPath)) {
                    final String resolvedPath = resolveTildeInPath(currentOs, systemUserHome, providedPath);
                    if (!resolvedPath.equals(providedPath)) {
                        field.set(detectConfiguration, resolvedPath);
                        logger.warn(String.format("We have resolved %s to %s. If this is not expected, please revise the path provided, or specify --detect.resolve.tilde.in.paths=false.", providedPath, resolvedPath));
                    }
                    field.setAccessible(wasAccessible);
                }
            }
        }
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
