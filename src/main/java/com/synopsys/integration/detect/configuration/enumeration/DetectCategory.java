package com.synopsys.integration.detect.configuration.enumeration;

import com.synopsys.integration.configuration.util.Category;

public class DetectCategory extends Category {
    public static final DetectCategory Advanced = new DetectCategory("advanced");
    public static final DetectCategory Simple = new DetectCategory("simple");

    protected DetectCategory(String name) {
        super(name);
    }
}
