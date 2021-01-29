/*
 * buildSrc
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
package com.synopsys.integration.detect.docs.model;

import java.util.List;

import com.synopsys.integration.detect.docs.copied.HelpJsonOption;

public class SplitGroup {
    private final String groupName;
    private final String superGroup;
    private final String location;
    private final List<HelpJsonOption> simple;
    private final List<HelpJsonOption> advanced;
    private final List<HelpJsonOption> deprecated;

    public SplitGroup(final String groupName, final String superGroup, final String location, final List<HelpJsonOption> simple, final List<HelpJsonOption> advanced, final List<HelpJsonOption> deprecated) {
        this.groupName = groupName;
        this.superGroup = superGroup;
        this.location = location;
        this.simple = simple;
        this.advanced = advanced;
        this.deprecated = deprecated;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getSuperGroup() {
        return superGroup;
    }

    public String getLocation() {
        return location;
    }

    public List<HelpJsonOption> getSimple() {
        return simple;
    }

    public List<HelpJsonOption> getAdvanced() {
        return advanced;
    }

    public List<HelpJsonOption> getDeprecated() {
        return deprecated;
    }
}
