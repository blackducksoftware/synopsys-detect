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
import java.util.Map;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.Packager;
import com.blackducksoftware.integration.hub.packman.packagemanager.rubygems.parsers.SimpleParser;

public class RubygemsPackager extends Packager {
    private final String gemlock;

    public RubygemsPackager(final String gemlock) {
        this.gemlock = gemlock;
    }

    @Override
    public List<DependencyNode> makeDependencyNodes() {
        final SimpleParser gemlockParser = new SimpleParser("  ", ":");
        final Map<String, Map<String, Object>> gemlockMap = gemlockParser.parse(gemlock);

        gemlockMap.get("DEPENDENCIES").entrySet().forEach(entry -> {
            entry.getKey();
        });
        return null;
    }
}
