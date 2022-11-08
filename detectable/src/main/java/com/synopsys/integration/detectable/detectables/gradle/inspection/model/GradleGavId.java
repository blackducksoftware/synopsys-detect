package com.synopsys.integration.detectable.detectables.gradle.inspection.model;

import com.synopsys.integration.bdio.graph.builder.LazyId;

public interface GradleGavId {
    LazyId toDependencyId();
}
