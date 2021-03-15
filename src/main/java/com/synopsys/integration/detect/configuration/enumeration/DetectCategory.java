/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration.enumeration;

import com.synopsys.integration.configuration.util.Category;

public class DetectCategory extends Category {
    public static final DetectCategory Advanced = new DetectCategory("advanced");
    public static final DetectCategory Simple = new DetectCategory("simple");

    protected DetectCategory(final String name) {
        super(name);
    }
}
