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
package com.synopsys.integration.configuration.help;

import com.synopsys.integration.configuration.property.Property;

public class PropertyInfo {
    private String key;
    private String value;
    private Property property;

    public PropertyInfo(final String key, final String value, final Property property) {
        this.key = key;
        this.value = value;
        this.property = property;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Property getProperty() {
        return property;
    }
}
