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
package com.synopsys.integration.configuration.config;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PropertyMap<T extends Object> {
    private Map<String, T> map;

    public PropertyMap(final Map<String, T> map) {
        this.map = map;
    }

    public Map<String, T> getMap() {
        return map;
    }

    public List<String> getKeys() {
        return new LinkedList<>(map.keySet());
    }

    public List<T> getValues() {
        return new LinkedList<>(map.values());
    }

    public Map<String, T> getSortedMap() {
        return map.entrySet().stream()
                   .sorted(Map.Entry.comparingByKey())
                   .collect(Collectors.toMap(
                       Map.Entry::getKey,
                       Map.Entry::getValue,
                       (oldValue, newValue) -> oldValue, LinkedHashMap::new));

    }
}
