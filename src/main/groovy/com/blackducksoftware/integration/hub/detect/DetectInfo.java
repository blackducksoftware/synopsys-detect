/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.util.ResourceUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@Component
public class DetectInfo {

    @Autowired
    Gson gson;

    public void init() {
        try {
            buildInfo = gson.fromJson(ResourceUtil.getResourceAsString("buildInfo.json", StandardCharsets.UTF_8.toString()), BuildInfo.class);
        } catch (JsonSyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BuildInfo buildInfo;

    public String getDetectVersion() {
        return buildInfo.detectVersion;
    }

}
