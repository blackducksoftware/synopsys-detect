package com.synopsys.integration.detectable.detectables.sbt.plugin;

import java.util.Optional;

public interface SbtPluginLineParser {
    Optional<SbtNode> tryParseLine(String line);
}
