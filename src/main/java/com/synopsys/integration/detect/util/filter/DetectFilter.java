package com.synopsys.integration.detect.util.filter;

public interface DetectFilter {
    boolean shouldInclude(String itemName);
}
