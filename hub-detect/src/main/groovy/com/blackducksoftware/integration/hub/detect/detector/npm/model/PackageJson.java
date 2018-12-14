package com.blackducksoftware.integration.hub.detect.detector.npm.model;

import java.util.HashMap;
import java.util.Map;

public class PackageJson {
    public Map<String, String> dependencies = new HashMap<>();
    public Map<String, String> devDependencies = new HashMap<>();
}
