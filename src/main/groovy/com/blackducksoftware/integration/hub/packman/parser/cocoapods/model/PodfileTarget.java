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
import java.util.List;

public class PodfileTarget {
    final public String name;

    public List<Pod> pods = new ArrayList<>();

    public PodfileTarget parent;

    public boolean isAbstract = false;

    public PodfileTarget(final String name) {
        this.name = name;
    }
}
