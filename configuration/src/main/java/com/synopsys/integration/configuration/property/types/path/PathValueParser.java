/*
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

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.parse.ValueParser;

public class PathValueParser extends ValueParser<PathValue> {
    @NotNull
    @Override
    public PathValue parse(@NotNull final String value) throws ValueParseException {
        final String trimmedValue = value.trim();
        if (StringUtils.isNotBlank(trimmedValue)) {
            return new PathValue(trimmedValue);
        } else {
            throw new ValueParseException(trimmedValue, "Path", "A path must have at least one non-whitespace character!");
        }
    }
}
