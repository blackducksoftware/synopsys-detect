package com.synopsys.integration.detect.fastsca.options;

import com.synopsys.integration.detect.configuration.enumeration.ScaStrategy;
import java.io.File;

public class ScaOptions {
    private final Enum<ScaStrategy> strategy;
    private final String kbHost;
    private final String licenseKey;
    private final File outputDirectory;

    public ScaOptions(Enum<ScaStrategy> strategy, String kbHost, String licenseKey, File outputDirectory) {
        this.strategy = strategy;
        this.kbHost = kbHost;
        this.licenseKey = licenseKey;
        this.outputDirectory = outputDirectory;
    }

    public Enum<ScaStrategy> getStrategy() {
        return strategy;
    }

    public String getKbHost() {
        return kbHost;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public File getOutputFile() {
        return outputDirectory;
    }
}