package com.synopsys.integration.detect.lifecycle.boot.product.version;

import java.util.LinkedList;
import java.util.List;

import com.synopsys.integration.blackduck.version.BlackDuckVersion;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;

public class BlackDuckMinimumVersionChecks {
    private final List<BlackDuckMinimumVersionCheck> checks;

    public BlackDuckMinimumVersionChecks() {
        checks = new LinkedList<>();

        checks.add(new BlackDuckMinimumVersionCheck(
            "Rapid mode",
            o -> o.getBlackDuckScanMode() == BlackduckScanMode.RAPID,
            new BlackDuckVersion(2021, 6, 0)
        ));

        checks.add(new BlackDuckMinimumVersionCheck(
                "Stateless mode",
                o -> o.getBlackDuckScanMode() == BlackduckScanMode.STATELESS,
                new BlackDuckVersion(2021, 6, 0)
            ));

        checks.add(new BlackDuckMinimumVersionCheck(
            "IaC scan",
            o -> o.getDetectToolFilter().shouldInclude(DetectTool.IAC_SCAN),
            new BlackDuckVersion(2021, 6, 0)
        ));

        checks.add(new BlackDuckMinimumVersionCheck(
                "Stateless signature scan",
                o -> o.getDetectToolFilter().shouldInclude(DetectTool.SIGNATURE_SCAN) && o.getBlackDuckScanMode() == BlackduckScanMode.STATELESS,
                new BlackDuckVersion(2022, 10, 0)
        ));
    }

    public List<BlackDuckMinimumVersionCheck> get() {
        return checks;
    }
}
