package com.synopsys.integration.detectable.detectable.explanation;

public class FoundSbtPlugin extends Explanation {
    private final String pluginName;

    public FoundSbtPlugin(String pluginName) {
        this.pluginName = pluginName;
    }

    @Override
    public String describeSelf() {
        return "Found sbt plugin: " + pluginName;
    }
}
