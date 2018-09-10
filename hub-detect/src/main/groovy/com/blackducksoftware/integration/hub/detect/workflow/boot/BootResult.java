package com.blackducksoftware.integration.hub.detect.workflow.boot;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;

public class BootResult {
    public BootType bootType;
    public DetectContext detectContext;

    public enum BootType {
        EXIT,
        CONTINUE
    }

    public  static  BootResult exit() {
        BootResult result = new BootResult();
        result.bootType = BootType.EXIT;
        return  result;
    }
}
