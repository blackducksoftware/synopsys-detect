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
package com.blackducksoftware.integration.hub.packman.parser.cocoapods.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.google.gson.Gson;

public class PodLock {

    public List<DependencyNode> pods = new ArrayList<>();

    public List<DependencyNode> dependencies = new ArrayList<>();

    public Map<String, String> specChecsums = new HashMap<>();

    public Map<String, String> externalSources = new HashMap<>();

    public String podfileChecksum;

    public String cococapodsVersion;

    @Autowired
    public Gson gson;

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
