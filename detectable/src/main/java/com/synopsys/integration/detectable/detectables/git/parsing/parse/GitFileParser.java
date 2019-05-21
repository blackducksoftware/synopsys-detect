package com.synopsys.integration.detectable.detectables.git.parsing.parse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigElement;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class GitFileParser {
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    public String parseGitHead(final InputStream inputStream) throws IOException {
        final String line = IOUtils.toString(inputStream, StandardCharsets.UTF_8).trim();
        return line.replaceFirst("ref:\\w*", "").trim();
    }

    public List<GitConfigElement> parseGitConfig(final InputStream inputStream) throws IOException {
        final List<String> gitConfigLines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);

        final List<GitConfigElement> gitConfigElements = new ArrayList<>();
        final List<String> lineBuffer = new ArrayList<>();
        for (final String rawLine : gitConfigLines) {
            final String line = StringUtils.stripToEmpty(rawLine);

            if (StringUtils.isBlank(line)) {
                continue;
            }

            if (isGitConfigElementStart(line)) {
                final Optional<GitConfigElement> gitConfigElement = processGitConfigElementLines(lineBuffer);
                gitConfigElement.ifPresent(gitConfigElements::add);
                lineBuffer.clear();
            }

            lineBuffer.add(line);
        }

        final Optional<GitConfigElement> gitConfigElement = processGitConfigElementLines(lineBuffer);
        gitConfigElement.ifPresent(gitConfigElements::add);

        return gitConfigElements;
    }

    private boolean isGitConfigElementStart(final String line) {
        return line.startsWith("[") && line.endsWith("]");
    }

    private Optional<GitConfigElement> processGitConfigElementLines(final List<String> lines) {
        final Map<String, String> properties = new HashMap<>();
        String elementType = null;
        String elementName = null;

        for (final String line : lines) {
            if (isGitConfigElementStart(line)) {
                final String lineWithoutBrackets = line.replace("[", "").replace("]", "");
                final String[] pieces = lineWithoutBrackets.split(" ");

                if (pieces.length == 1) {
                    elementType = pieces[0].trim();
                } else if (pieces.length == 2) {
                    elementType = pieces[0].trim();
                    elementName = pieces[1].replace("\"", "").trim();
                } else {
                    logger.warn(String.format("Invalid git config element. Skipping. %s", line));
                    break;
                }
            } else {
                final String[] pieces = line.split("=");

                if (pieces.length == 2) {
                    final String propertyKey = pieces[0].trim();
                    final String propertyValue = pieces[1].trim();
                    properties.put(propertyKey, propertyValue);
                } else {
                    logger.warn(String.format("Invalid git config element property. Skipping. %s", line));
                }
            }
        }

        if (StringUtils.isNotBlank(elementType)) {
            return Optional.of(new GitConfigElement(elementType, elementName, properties));
        } else {
            return Optional.empty();
        }
    }
}
