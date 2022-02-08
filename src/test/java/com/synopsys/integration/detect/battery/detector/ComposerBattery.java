package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;

@Tag("battery")
public class ComposerBattery {
    @Test
    void lock() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("composer-lock");
        test.sourceDirectoryNamed("linux-composer");
        test.sourceFileFromResource("composer.json");
        test.sourceFileFromResource("composer.lock");
        test.git("git@sig-gitlab.internal.synopsys.com:blackduck/integrations/integrations-tests/aura-sql.git", "master");
        test.expectBdioResources();
        test.run();
    }
}

