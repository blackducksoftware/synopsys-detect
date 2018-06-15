/**
 * detect-common
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
package com.blackducksoftware.integration.hub.detect.help.html;

public class HelpHtmlOption {
    public String key;
    public String defaultValue;
    public String description;
    public String acceptableValues;
    public String detailedDescription;
    public String deprecationNotice;

    public HelpHtmlOption(final String key, final String defaultValue, final String description, final String acceptableValues, final String detailedDescription, final String deprecationNotice) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.description = description;
        this.acceptableValues = acceptableValues;
        this.detailedDescription = detailedDescription;
        this.deprecationNotice = deprecationNotice;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public String getDeprecationNotice() {
        return deprecationNotice;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getAcceptableValues() {
        return acceptableValues;
    }
}