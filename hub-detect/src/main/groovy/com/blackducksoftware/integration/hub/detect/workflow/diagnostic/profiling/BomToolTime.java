package com.blackducksoftware.integration.hub.detect.workflow.diagnostic.profiling;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;

public class BomToolTime {
    private final long ms;
    private final BomTool bomTool;

    public BomToolTime(final BomTool bomTool, final long ms) {
        this.ms = ms;
        this.bomTool = bomTool;
    }

    public long getMs() {
        return ms;
    }

    public BomTool getBomTool() {
        return bomTool;
    }
}
