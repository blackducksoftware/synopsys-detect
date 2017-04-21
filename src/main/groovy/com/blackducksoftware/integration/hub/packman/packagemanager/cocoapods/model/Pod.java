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

public class Pod {
    final public String name;

    public String otherInfo;

    public String gitUrl;

    public String gitBranch;

    public String gitCommit;

    public String gitTag;

    public Pod(final String name) {
        this.name = name;
    }
}
