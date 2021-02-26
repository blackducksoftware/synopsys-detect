/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bazel.pipeline.step.model;

import com.synopsys.integration.util.Stringable;

public class ResultItem extends Stringable {
    private Target target;

    public Target getTarget() {
        return target;
    }
}
