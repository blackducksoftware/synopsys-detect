package com.synopsys.integration.detectable.detectables.sbt.parse.model;

import com.synopsys.integration.util.Stringable;

public class SbtAggregate extends Stringable {
    private String name;
    private String org;
    private String version;

    public SbtAggregate(String name, String org, String version) {
        this.name = name;
        this.org = org;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
