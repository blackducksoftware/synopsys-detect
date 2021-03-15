/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.cli.model.json.v1;

import java.util.Map;

public class IssueSummaryV1 {
    public Map<String, Integer> issuesBySeverity;
    public String summaryUrl;
    public int total;

}
