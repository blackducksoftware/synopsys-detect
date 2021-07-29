/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.util.List;

import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.rest.HttpUrl;

public class RapidScanResult {
    private List<HttpUrl> scanIds;
    private List<DeveloperScanComponentResultView> componentResultViews;

    public RapidScanResult(List<HttpUrl> scanIds, List<DeveloperScanComponentResultView> componentResultViews) {
        this.scanIds = scanIds;
        this.componentResultViews = componentResultViews;
    }

    public static RapidScanResult forSingleScan(HttpUrl url, List<DeveloperScanComponentResultView> componentResultViews) {
        return new RapidScanResult(Bds.of(url).toList(), componentResultViews);
    }

    public List<HttpUrl> getScanIds() {
        return scanIds;
    }

    public List<DeveloperScanComponentResultView> getComponentResultViews() {
        return componentResultViews;
    }
}
