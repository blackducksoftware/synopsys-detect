package com.blackduck.integration.detect.util.filter;

public interface DetectFilter {
    boolean shouldInclude(String itemName);
}
