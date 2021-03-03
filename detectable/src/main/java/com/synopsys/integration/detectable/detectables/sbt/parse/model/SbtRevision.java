/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.sbt.parse.model;

import java.util.List;

public class SbtRevision {
    private final String name;
    private final List<SbtCaller> callers;

    public SbtRevision(final String name, final List<SbtCaller> callers) {
        this.name = name;
        this.callers = callers;
    }

    public String getName() {
        return name;
    }

    public List<SbtCaller> getCallers() {
        return callers;
    }

}
