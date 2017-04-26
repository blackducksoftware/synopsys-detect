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
package com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.model;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;

public class PodLock {

    public List<DependencyNode> pods = new ArrayList<>();

    public List<DependencyNode> dependencies = new ArrayList<>();

    public String cococapodsVersion;
}
