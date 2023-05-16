package com.synopsys.integration.detect.fastsca.options;

import com.synopsys.integration.detect.configuration.enumeration.ScaStrategy;
import java.io.File;
import java.util.UUID;

public class ScaOptions {
    private final Enum<ScaStrategy> strategy;
    private final String kbScheme;
    private final String kbHost;
    private final int kbPort;
    private final String licenseKey;
    private final String user;
    private final File outputDirectory;
    private final String invalidLicenseKey = UUID.randomUUID().toString();

    public ScaOptions(Enum<ScaStrategy> strategy, String kbScheme, String kbHost, int kbPort, String licenseKey, String user, File outputDirectory) {
        this.strategy = strategy;
        this.kbScheme = kbScheme;
        this.kbHost = kbHost;
        this.kbPort = kbPort;
        this.licenseKey = licenseKey;
        this.user = user;
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

    public String getKbScheme() {
        return kbScheme;
    }

    public int getKbPort() {
        return kbPort;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public String getUser() {
        return user;
    }

    public String getInvalidLicenseKey() {
        return invalidLicenseKey;
    }
}