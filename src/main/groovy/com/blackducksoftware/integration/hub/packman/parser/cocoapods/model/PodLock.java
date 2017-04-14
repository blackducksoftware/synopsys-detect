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

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;

public class PodLock {

    public List<DependencyNode> pods = new ArrayList<>();

    public List<DependencyNode> dependencies = new ArrayList<>();

    public Map<String, String> specChecsums = new HashMap<>();

    public String podfileChecksum;

    public String cococapodsVersion;

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("PODS:\n");
        for (final DependencyNode p : pods) {
            builder.append("  - ");
            builder.append(p.name);
            builder.append(" (");
            builder.append(p.version);
            builder.append(")\n");
            for (final DependencyNode dep : p.children) {
                builder.append("    - ");
                builder.append(dep.name);
                builder.append(" (");
                builder.append(dep.version);
                builder.append(")\n");
            }
        }
        builder.append("\nDEPENDENCIES:\n");
        for (final DependencyNode p : dependencies) {
            builder.append("  - ");
            builder.append(p.name);
            builder.append(" (");
            builder.append(p.version);
            builder.append(")\n");
        }
        builder.append("\nSPEC CHECKSUMS:\n");
        for (final DependencyNode p : dependencies) {
            builder.append("  ");
            builder.append(p.name);
            builder.append(": ");
            builder.append(specChecsums.get(p.name));
            builder.append("\n");
        }
        builder.append("\nPODFILE CHECKSUM: " + podfileChecksum + "\n");
        builder.append("\nCOCOAPODS: " + cococapodsVersion + "\n");
        return builder.toString();
    }
}
