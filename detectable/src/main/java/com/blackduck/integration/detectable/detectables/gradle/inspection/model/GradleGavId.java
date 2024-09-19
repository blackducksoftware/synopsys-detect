package com.blackduck.integration.detectable.detectables.gradle.inspection.model;

import com.blackduck.integration.bdio.graph.builder.LazyId;

public interface GradleGavId {
    LazyId toDependencyId();
}
