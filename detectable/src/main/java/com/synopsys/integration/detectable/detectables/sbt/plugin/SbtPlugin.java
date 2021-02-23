package com.synopsys.integration.detectable.detectables.sbt.plugin;

import java.util.List;

public class SbtPlugin {
    private final String name;
    private final List<String> arguments;
    private final SbtPluginLineParser parser;

    public SbtPlugin(final String name, final List<String> arguments, final SbtPluginLineParser parser) {
        this.name = name;
        this.arguments = arguments;
        this.parser = parser;
    }

    public String getName() {
        return name;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public SbtPluginLineParser getLineParser() {
        return parser;
    }
}
