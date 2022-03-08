package com.synopsys.integration.detectable.detectables.go.gomod.parse;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.synopsys.integration.detectable.detectables.go.gomod.GoVersion;

public class GoVersionParser {
    // java:S5852: Warning about potential DoS risk.
    @SuppressWarnings({ "java:S5852" })
    private static final Pattern GENERATE_GO_LIST_JSON_OUTPUT_PATTERN = Pattern.compile("\\d+\\.[\\d.]+"); // Example: "go version go1.17.5 darwin/amd64" -> ""

    public Optional<GoVersion> parseGoVersion(String goVersionLine) {
        Matcher matcher = GENERATE_GO_LIST_JSON_OUTPUT_PATTERN.matcher(goVersionLine);
        if (matcher.find()) {
            String version = matcher.group(); // 1.16.5
            String[] parts = version.split("\\.");
            int majorVersion = Integer.parseInt(parts[0]);
            int minorVersion = Integer.parseInt(parts[1]);
            return Optional.of(new GoVersion(majorVersion, minorVersion));
        }
        return Optional.empty();
    }
}
