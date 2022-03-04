package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.util.List;

import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.rest.HttpUrl;

public class RapidScanResult {
    private final List<HttpUrl> scanIds;
    private final List<DeveloperScanComponentResultView> componentResultViews;

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
