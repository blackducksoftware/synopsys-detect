package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;

@Tag("battery")
public class ComposerBattery {
    @Test
    void lock() {
        final BatteryTest test = new BatteryTest("composer-lock");
        test.sourceDirectoryNamed("linux-composer");
        test.sourceFileFromResource("composer.json");
        test.sourceFileFromResource("composer.lock");
        test.git("git@sig-gitlab.internal.synopsys.com:blackduck/integrations/integrations-tests/aura-sql.git", "master");
        test.expectBdioResources();
        test.run();
    }
}

