package com.blackducksoftware.integration.hub.detect.lifecycle.boot;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;

public class BootResult {
    public BootType bootType;
    public DetectConfiguration detectConfiguration; //Application needs this to make sure exit code behaves.

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
