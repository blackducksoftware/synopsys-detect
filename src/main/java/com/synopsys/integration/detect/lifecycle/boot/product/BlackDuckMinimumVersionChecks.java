package com.synopsys.integration.detect.lifecycle.boot.product;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectPropertyConfiguration;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;

public class BlackDuckMinimumVersionChecks {

    public List<Pair<Predicate<DetectPropertyConfiguration>, BlackDuckVersion>> create() {
        List<Pair<Predicate<DetectPropertyConfiguration>, BlackDuckVersion>> table = new LinkedList<>();

        // Rapid Sig Scan: 2022.10.0
        table.add(new ImmutablePair<>(
            c -> c.getValue(DetectProperties.DETECT_TOOLS).containsValue(DetectTool.SIGNATURE_SCAN)
                && c.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE) == BlackduckScanMode.RAPID,
            new BlackDuckVersion(2022, 10, 0)
        ));

        // Rapid version: 2021.6.0
        table.add(new ImmutablePair<>(
            c -> c.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE) == BlackduckScanMode.RAPID,
            new BlackDuckVersion(2021, 6, 0)
        ));

        // IaC 2022.7.0
        table.add(new ImmutablePair<>(
            c -> c.getValue(DetectProperties.DETECT_TOOLS).containsValue(DetectTool.IAC_SCAN),
            new BlackDuckVersion(2021, 6, 0)
        ));

        return table;
    }

}
