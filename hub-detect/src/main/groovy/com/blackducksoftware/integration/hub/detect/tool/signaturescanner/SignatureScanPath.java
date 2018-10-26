package com.blackducksoftware.integration.hub.detect.tool.signaturescanner;

import java.util.HashSet;
import java.util.Set;

public class SignatureScanPath {
    public String targetPath;
    public Set<String> exclusions = new HashSet<>();
}
