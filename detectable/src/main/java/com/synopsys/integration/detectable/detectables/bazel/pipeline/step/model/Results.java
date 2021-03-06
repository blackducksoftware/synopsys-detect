/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bazel.pipeline.step.model;

import java.util.List;

import com.synopsys.integration.util.Stringable;

public class Results extends Stringable {
    private List<ResultItem> resultItems;

    public List<ResultItem> getResultItems() {
        return resultItems;
    }
}
