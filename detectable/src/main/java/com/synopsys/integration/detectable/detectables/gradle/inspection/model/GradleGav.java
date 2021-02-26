/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.gradle.inspection.model;

import com.synopsys.integration.bdio.model.dependencyid.StringDependencyId;

public class GradleGav implements GradleGavId {
    private final String name;
    private final String group;
    private final String version;

    public GradleGav(String name, String group, String version) {
        this.name = name;
        this.group = group;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public StringDependencyId toDependencyId() {
        return new StringDependencyId(String.format("%s:%s:%s", getName(), getGroup(), getVersion()));
    }
}
