package com.blackducksoftware.integration.hub.detect.util.filter;

public interface DetectFilter {
    boolean shouldInclude(String itemName);
}
