/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.explanation;

public class FoundSbtPlugin extends Explanation {
    private final String pluginName;

    public FoundSbtPlugin(String pluginName) {
        this.pluginName = pluginName;
    }

    @Override
    public String describeSelf() {
        return "Found sbt plugin: " + pluginName;
    }
}
