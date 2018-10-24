package com.blackducksoftware.integration.hub.detect.lifecycle.boot;

public class BootResult {
    public BootType bootType;
    public DetectRunDependencies detectRunDependencies;

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
