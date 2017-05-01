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
package com.blackducksoftware.integration.hub.packman.packagemanager.rubygems;

import java.util.List;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.packagemanager.rubygems.parsers.GemlockParser;

public class RubygemsPackager {
    private final String gemlock;

    public RubygemsPackager(final String gemlock) {
        this.gemlock = gemlock;
    }

    public List<DependencyNode> makeDependencyNodes() {
        final GemlockParser gemlockParser = new GemlockParser();
        return gemlockParser.parse(gemlock);
    }
}
