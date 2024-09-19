package com.blackduck.integration.detect;

public class ApplicationUpdaterUtility {
    protected String getSysEnvProperty(String name) {
        return System.getenv(name);
    }
}