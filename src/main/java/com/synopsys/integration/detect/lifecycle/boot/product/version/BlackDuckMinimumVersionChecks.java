package com.synopsys.integration.detect.lifecycle.boot.product.version;

import java.util.LinkedList;
import java.util.List;

import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;

public class BlackDuckMinimumVersionChecks {
    private final List<BlackDuckMinimumVersionCheck> checks;

    public BlackDuckMinimumVersionChecks() {
        checks = new LinkedList<>();

        checks.add(new BlackDuckMinimumVersionCheck(
            "Rapid mode",
            c -> c.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE) == BlackduckScanMode.RAPID,
            new BlackDuckVersion(2021, 6, 0)
        ));

        checks.add(new BlackDuckMinimumVersionCheck(
            "Ephemeral mode",
            c -> c.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE) == BlackduckScanMode.EPHEMERAL,
            new BlackDuckVersion(2021, 6, 0)
        ));

        checks.add(new BlackDuckMinimumVersionCheck(
            "IaC scan",
            c -> (c.getValue(DetectProperties.DETECT_TOOLS).containsValue(DetectTool.IAC_SCAN) && !c.getValue(DetectProperties.DETECT_TOOLS_EXCLUDED)
                .containsValue(DetectTool.IAC_SCAN)),
            new BlackDuckVersion(2021, 6, 0)
        ));

        checks.add(new BlackDuckMinimumVersionCheck(
            "Ephemeral signature scan",
            c -> (c.getValue(DetectProperties.DETECT_TOOLS).containsValue(DetectTool.SIGNATURE_SCAN) && !c.getValue(DetectProperties.DETECT_TOOLS_EXCLUDED)
                .containsValue(DetectTool.SIGNATURE_SCAN))
                && c.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE) == BlackduckScanMode.EPHEMERAL,
            new BlackDuckVersion(2022, 10, 0)
        ));
    }

    public List<BlackDuckMinimumVersionCheck> get() {
        return checks;
    }
}
