package com.blackducksoftware.integration.hub.detect.lifecycle.boot;

import com.blackducksoftware.integration.hub.detect.lifecycle.run.RunDependencies;

public class BootResult {
    public BootType bootType;
    public RunDependencies runDependencies;

    public enum BootType {
        EXIT,
        CONTINUE
    }

    public static BootResult exit() {
        BootResult result = new BootResult();
        result.bootType = BootType.EXIT;
        return result;
    }
}
