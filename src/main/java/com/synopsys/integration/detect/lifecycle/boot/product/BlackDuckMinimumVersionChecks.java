package com.synopsys.integration.detect.lifecycle.boot.product;

import java.util.LinkedList;
import java.util.List;

import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;

public class BlackDuckMinimumVersionChecks {

    public List<BlackDuckMinimumVersionCheck> create() {
        List<BlackDuckMinimumVersionCheck> table = new LinkedList<>();

        table.add(new BlackDuckMinimumVersionCheck(
            "Rapid mode",
            c -> c.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE) == BlackduckScanMode.RAPID,
            new BlackDuckVersion(2021, 6, 0)
        ));

        table.add(new BlackDuckMinimumVersionCheck(
            "IaC scan",
            c -> c.getValue(DetectProperties.DETECT_TOOLS).containsValue(DetectTool.IAC_SCAN),
            new BlackDuckVersion(2021, 6, 0)
        ));

        table.add(new BlackDuckMinimumVersionCheck(
            "Rapid signature scan",
            c -> c.getValue(DetectProperties.DETECT_TOOLS).containsValue(DetectTool.SIGNATURE_SCAN)
                && c.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE) == BlackduckScanMode.RAPID,
            new BlackDuckVersion(2022, 10, 0)
        ));

        return table;
    }

}
