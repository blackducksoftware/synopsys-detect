/**
 * detect-configuration
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
package com.blackducksoftware.integration.hub.detect.help.json;

import java.util.ArrayList;
import java.util.List;

public class HelpJsonOption {
    public String propertyName = "";
    public String propertyKey = "";
    public String propertyType = "";
    public String defaultValue = "";
    public String addedInVersion = "";
    public String group = "";
    public List<String> additionalGroups = new ArrayList<>();
    public String description = "";
    public String detailedDescription = "";
    public boolean deprecated = false;
    public String deprecatedDescription = "";
    public String deprecatedFailInVersion = "";
    public String deprecatedRemoveInVersion = "";
    public boolean strictValues = false;
    public boolean caseSensitiveValues = false;
    public boolean hasAcceptableValues = false;
    public List<String> acceptableValues = new ArrayList<>();

}
