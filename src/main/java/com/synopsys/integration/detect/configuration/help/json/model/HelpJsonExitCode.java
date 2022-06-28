package com.synopsys.integration.detect.configuration.help.json.model;

public class HelpJsonExitCode {
    private String exitCodeKey = "";
    private String exitCodeDescription = "";
    private Integer exitCodeValue = 0;

    public Integer getExitCodeValue() {
        return exitCodeValue;
    }

    public void setExitCodeValue(Integer exitCodeValue) {
        this.exitCodeValue = exitCodeValue;
    }

    public String getExitCodeKey() {
        return exitCodeKey;
    }

    public void setExitCodeKey(String exitCodeKey) {
        this.exitCodeKey = exitCodeKey;
    }

    public String getExitCodeDescription() {
        return exitCodeDescription;
    }

    public void setExitCodeDescription(String exitCodeDescription) {
        this.exitCodeDescription = exitCodeDescription;
    }
}
