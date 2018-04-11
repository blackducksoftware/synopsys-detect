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
package com.blackducksoftware.integration.hub.detect.help.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;

public class HelpHtmlDataBuilder {

    private Map<String, HelpHtmlGroup> groupsByName = new HashMap<>();
    
    public HelpHtmlDataBuilder addDetectOption(DetectOption option) {
        String groupName = option.getHelp().primaryGroup;
        if (!groupsByName.containsKey(groupName)) {
            HelpHtmlGroup group = new HelpHtmlGroup();
            group.groupName = groupName;
            group.options = new ArrayList<>();
            groupsByName.put(groupName, group);
        }
        
        HelpHtmlGroup group = groupsByName.get(groupName);
        
        String description = option.getHelp().description;
        if (option.getAcceptableValues().size() > 0) {
            description += " (" + option.getAcceptableValues().stream().collect(Collectors.joining("|")) + ")";
        }
        HelpHtmlOption htmlOption = new HelpHtmlOption(option.getKey(), option.getDefaultValue(), description);
        group.options.add(htmlOption);
        return this;
    }
    
    
    public HelpHtmlData build() {
        return new HelpHtmlData(new ArrayList<>(groupsByName.values()));
    }
}
