/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
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
