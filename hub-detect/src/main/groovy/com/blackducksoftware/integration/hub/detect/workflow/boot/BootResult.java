package com.blackducksoftware.integration.hub.detect.workflow.boot;

public class BootResult {
    public BootType bootType;
    public DetectRunContext detectRunContext;

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
