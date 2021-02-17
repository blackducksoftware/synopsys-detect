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
package com.synopsys.integration.configuration.property.base;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.property.Property;

/**
 * A property whose values are all prefixed with a common key.
 *
 * The key is lowercase and dot separated, ending with a dot. For example "docker."
 * When retrieved from a Configuration, keys will be returned without the starting prefix. For example "docker.enabled.key" should be returned as "enabled.key" when the key is "docker."
 */
public class PassthroughProperty extends Property {
    public PassthroughProperty(@NotNull final String key) {
        super(key);
    }

    public String trimKey(String givenKey) {
        return givenKey.substring(getKey().length() + 1);
    }
}