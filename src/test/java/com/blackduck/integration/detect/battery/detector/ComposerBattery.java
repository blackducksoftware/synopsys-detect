package com.blackduck.integration.detect.battery.detector;

import com.blackduck.integration.detect.battery.util.DetectorBatteryTestRunner;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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

