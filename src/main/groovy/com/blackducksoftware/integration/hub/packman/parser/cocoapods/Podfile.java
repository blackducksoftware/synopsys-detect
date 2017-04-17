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
package com.blackducksoftware.integration.hub.packman.parser.cocoapods;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;

public class Podfile {

    public List<DependencyNode> targets = new ArrayList<>();

    public String platform;

    public String useFramworks;

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (platform != null) {
            builder.append("platform :" + platform + "\n\n");
        }
        for (final DependencyNode p : targets) {
            builder.append("target '");
            builder.append(p.name);
            builder.append("' do\n");
            for (final DependencyNode dep : p.children) {
                builder.append("  pod '");
                builder.append(dep.name);
                builder.append("', '");
                builder.append(dep.version);
                builder.append("'\n");
            }
            builder.append("end\n\n");
        }
        return builder.toString();
    }
}
