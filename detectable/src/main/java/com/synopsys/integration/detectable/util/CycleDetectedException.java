package com.synopsys.integration.detectable.util;

import com.synopsys.integration.bdio.model.dependency.Dependency;

public class CycleDetectedException extends Exception {
    private static final long serialVersionUID = -6549724080144281634L;

    public CycleDetectedException(Dependency dependency) {
        super(String.format("A cycle was detected with dependency %s, this is not supported. Please contact support. ", dependency.toString()));
    }
}
