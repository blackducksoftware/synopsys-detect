package com.synopsys.integration.detectable.detectables.conan.cli.parser.conan2.model;

import com.synopsys.integration.util.Stringable;

public class ConanGraphInfoDependency extends Stringable {
    boolean direct;

    boolean build;

    public ConanGraphInfoDependency(boolean direct, boolean build) {
        this.direct = direct;
        this.build = build;
    }

    public boolean isDirect() {
        return direct;
    }

    public boolean isBuild() {
        return build;
    }
}
