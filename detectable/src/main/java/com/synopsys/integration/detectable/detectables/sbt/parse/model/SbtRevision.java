package com.synopsys.integration.detectable.detectables.sbt.parse.model;

import java.util.List;

public class SbtRevision {
    private final String name;
    private final List<SbtCaller> callers;

    public SbtRevision(String name, List<SbtCaller> callers) {
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
