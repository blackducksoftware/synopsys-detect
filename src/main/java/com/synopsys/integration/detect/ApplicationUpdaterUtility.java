package com.synopsys.integration.detect;

public class ApplicationUpdaterUtility {
    protected String getSysEnvProperty(String name) {
        return System.getenv(name);
    }
}