package com.blackducksoftware.integration.hub.detect.configuration;

public enum PropertyAuthority {
    None, //anyone can access this property
    DirectoryManager,
    BootManager,
    AirGapManager;
}