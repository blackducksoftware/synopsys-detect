package com.synopsys.integration.detectable.detectables.bitbake.model;

import java.util.Optional;

public class BitbakeEnvironment {
    private final String machineArch;
    private final String licensesDirPath;

    public BitbakeEnvironment(final String machineArch, final String licensesDirPath) {
        this.machineArch = machineArch;
        this.licensesDirPath = licensesDirPath;
    }

    public Optional<String> getMachineArch() {
        return Optional.ofNullable(machineArch);
    }

    public Optional<String> getLicensesDirPath() {
        return Optional.ofNullable(licensesDirPath);
    }
}
