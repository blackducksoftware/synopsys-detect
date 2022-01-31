package com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class GemspecLineParser {
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(getClass()));

    private static final String COMMENT_START_TOKEN = "#";
    private static final String QUOTE_TOKEN = "'";

    public boolean shouldParseLine(String line) {
        return Arrays.stream(GemspecDependencyType.values())
            .map(gemspecDependencyType -> line.contains(gemspecDependencyType.getToken()))
            .anyMatch(shouldParse -> shouldParse.equals(true));
    }

    public Optional<GemspecDependency> parseLine(String line) {
        try {
            Optional<LineStart> lineStart = findLineStart(line);
            if (!lineStart.isPresent()) {
                // Not a line containing dependency info
                return Optional.empty();
            }

            Optional<String> lineRemainder = lineStart.map(LineStart::getStartingIndex)
                .map(line::substring)
                .map(this::normalizeQuotes)
                .map(this::stripParentheses)
                .map(this::stripTrailingComment)
                .map(StringUtils::stripToNull);

            Optional<String> name = lineRemainder
                .map(this::normalizeWeirdCharacters)
                .map(this::extractName)
                .map(this::stripFileExtension);

            Optional<String> version = lineRemainder.map(this::extractVersion);

            GemspecDependencyType gemspecDependencyType = lineStart.get().getGemspecDependencyType();
            if (name.isPresent() && version.isPresent()) {
                return Optional.of(new GemspecDependency(name.get(), version.get(), gemspecDependencyType));
            } else if (name.isPresent()) {
                return Optional.of(new GemspecDependency(name.get(), gemspecDependencyType));
            } else {
                throw new IntegrationException("Component name not found");
            }
        } catch (Exception e) {
            logger.debug(String.format("Failed to extract name and version from line: %s", line));
            logger.debug(e.getMessage(), e);
        }

        return Optional.empty();
    }

    private Optional<LineStart> findLineStart(String line) {
        LineStart lineStart = null;

        for (GemspecDependencyType dependencyType : GemspecDependencyType.values()) {
            int index = line.indexOf(dependencyType.getToken());

            if (index >= 0) {
                int startingIndex = index + dependencyType.getToken().length();
                lineStart = new LineStart(startingIndex, dependencyType);
                break;
            }
        }

        return Optional.ofNullable(lineStart);
    }

    // Example: gem.add_dependency('fakegem', '>= 1.0.0')
    private String stripParentheses(String line) {
        return line.replace("(", " ").replace(")", " ");
    }

    private String normalizeQuotes(String line) {
        return line.replace("\"", QUOTE_TOKEN);
    }

    // %q<fakegem>, '>= 1.0.0'
    private String normalizeWeirdCharacters(String line) {
        String normalized = line.replace("%q<", QUOTE_TOKEN);
        if (!line.equals(normalized)) {
            normalized = normalized.replaceFirst(">", QUOTE_TOKEN);
        }

        return normalized;
    }

    // Example: gem.add_dependency 'fakegem', '>= 1.0.0' # I am an inline comment
    private String stripTrailingComment(String line) {
        int commentIndex = line.indexOf(COMMENT_START_TOKEN);
        if (commentIndex < 0) {
            return line;
        }
        return line.substring(0, commentIndex);
    }

    // Example: fakegem.rb -> fakegem
    private String stripFileExtension(String name) {
        int extensionIndex = name.lastIndexOf('.');
        if (extensionIndex < 0) {
            return name;
        }
        return name.substring(0, extensionIndex);
    }

    private String extractName(String line) {
        int openQuoteIndex = line.indexOf(QUOTE_TOKEN);
        if (openQuoteIndex < 0) {
            return "";
        }
        int closeQuoteIndex = line.indexOf(QUOTE_TOKEN, openQuoteIndex + 1);
        if (closeQuoteIndex < 0) {
            return "";
        }
        String name = line.substring(openQuoteIndex + 1, closeQuoteIndex);

        return StringUtils.stripToNull(name);
    }

    private String extractVersion(String line) {
        if (!line.contains(",")) {
            return null;
        }

        String versionText = line.substring(line.indexOf(','));
        String[] versionSegments = versionText.replace("[", "").replace("]", "").split(",");

        return Arrays.stream(versionSegments)
            .map(segment -> segment.replace(QUOTE_TOKEN, ""))
            .map(StringUtils::stripToNull)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(", "));
    }

    private static class LineStart {
        private final int startingIndex;
        private final GemspecDependencyType gemspecDependencyType;

        private LineStart(int startingIndex, GemspecDependencyType gemspecDependencyType) {
            this.startingIndex = startingIndex;
            this.gemspecDependencyType = gemspecDependencyType;
        }

        public int getStartingIndex() {
            return startingIndex;
        }

        public GemspecDependencyType getGemspecDependencyType() {
            return gemspecDependencyType;
        }
    }
}
