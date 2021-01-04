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
package com.synopsys.integration.configuration.property.types.bool;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.parse.ValueParser;

class BooleanValueParser extends ValueParser<Boolean> {
    @NotNull
    @Override
    public Boolean parse(@NotNull final String value) throws ValueParseException {
        String trimmed = value.toLowerCase().trim();
        if (StringUtils.isBlank(trimmed)) {
            return true;
        } else {
            Boolean aBoolean = BooleanUtils.toBooleanObject(trimmed);
            if (aBoolean == null) {
                throw new ValueParseException(value, "boolean", "Unknown boolean format. Supported values include true and false.");
            } else {
                return aBoolean;
            }
        }
    }
}