/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bazel;

public class BazelProjectNameGenerator {

    public String generateFromBazelTarget(final String bazelTarget) {
        String projectName = bazelTarget
                                 .replaceAll("^//", "")
                                 .replaceAll("^:", "")
                                 .replace("/", "_")
                                 .replace(":", "_");
        return projectName;
    }
}
