package com.synopsys.integration.detectable.detectables.yarn.parse.entry.section;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;

public class YarnLockDependencySpecParser {
    private static final List<String> skippableProtocols = Arrays.asList("patch", "link", "portal");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;

    public YarnLockDependencySpecParser(YarnLockLineAnalyzer yarnLockLineAnalyzer) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
    }

    public Optional<YarnLockDependency> parse(String dependencySpec, boolean optional) {
        StringTokenizer tokenizer = TokenizerFactory.createDependencySpecTokenizer(dependencySpec);
        String name = yarnLockLineAnalyzer.unquote(tokenizer.nextToken());
        // version formats vary; see YarnLockDependencySpecParserTest
        String version = yarnLockLineAnalyzer.unquote(tokenizer.nextToken("").trim());
        if (version.startsWith(":")) {
            version = version.substring(1).trim();
        }
        version = yarnLockLineAnalyzer.unquote(version);
        logger.trace("\tdependency: name: {}, version: {} (optional: {})", name, version, optional);
        if (!hasSkippableProtocol(name, version)) {
            return Optional.of(new YarnLockDependency(name, version, optional));
        }
        return Optional.empty();
    }

    private boolean hasSkippableProtocol(String name, String version) {
        for (String skippableProtocol : skippableProtocols) {
            if (protocolMatches(version, skippableProtocol)) {
                logger.debug("{}@{} is a \"{}:\" dependency so will be skipped", name, version, skippableProtocol);
                return true;
            }
        }
        return false;
    }

    private boolean protocolMatches(String version, String protocol) {
        String protocolPrefix = protocol + ":";
        return version.startsWith((protocolPrefix));
    }
}
