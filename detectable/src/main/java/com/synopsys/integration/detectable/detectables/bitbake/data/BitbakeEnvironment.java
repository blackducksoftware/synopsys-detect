package com.synopsys.integration.detectable.detectables.bitbake.data;

import java.util.Optional;

import com.synopsys.integration.util.Stringable;

public class BitbakeEnvironment extends Stringable {
    private final String machineArch;
    private final String licensesDirPath;
    private final String machine;

    public BitbakeEnvironment(String machineArch, String licensesDirPath, String machine) {
        this.machineArch = machineArch;
        this.licensesDirPath = licensesDirPath;
        this.machine = machine;
    }

    public Optional<String> getMachineArch() {
        return Optional.ofNullable(machineArch);
    }

    public Optional<String> getLicensesDirPath() {
        return Optional.ofNullable(licensesDirPath);
    }

    public Optional<String> getMachine() {
        return Optional.ofNullable(machine);
    }
}
